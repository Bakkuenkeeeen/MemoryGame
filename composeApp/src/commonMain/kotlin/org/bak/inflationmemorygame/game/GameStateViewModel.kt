package org.bak.inflationmemorygame.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.components.VisualEffects
import org.bak.inflationmemorygame.dialogs.Dialogs
import org.bak.inflationmemorygame.isPreloadNeeded
import org.bak.inflationmemorygame.values.Constants
import org.bak.inflationmemorygame.values.LogMessages

class GameStateViewModel(
    initialStage: Int,
    playerCount: Int,
    shouldPreload: Boolean = isPreloadNeeded()
) : ViewModel() {

    private val stages = mutableStateListOf(StageState(stage = initialStage))
    private val players = mutableStateListOf<PlayerState>().apply {
        addAll(List(playerCount) { PlayerState() })
    }

    val currentStage: StageState by derivedStateOf { stages.maxBy { it.stage } }

    val currentPlayer: PlayerState by derivedStateOf {
        players[(currentStage.turns - 1) % players.size]
    }

    val logMessageState = LogMessageState(coroutineScope = viewModelScope)
    var isButtonsEnable: Boolean by mutableStateOf(true)
        private set

    private val additionalFlippedCards = mutableListOf<AbilityCard>()

    var isPreloading by mutableStateOf(shouldPreload)
        private set

    private val discoveredCards = mutableSetOf<String>()
    val dialogs = mutableStateListOf<Dialogs>()

    init {
        viewModelScope.launch {
            if (isPreloading) {
                preload()
            } else {
                startStage()
            }
        }
    }

    private suspend fun preload() {
        // TODO
        //  代表して1枚だけ表にすればいいと思っていたが、wasmだとロードしたカード以外が見えなくなる問題があり、
        //  暫定で全てロードする
        currentStage.cards.forEach { card ->
            card.changeSurface(isFaceUp = true)
            delay(Constants.PRELOAD_INTERVAL)
            card.changeSurface(isFaceUp = false)
        }
        delay(Constants.PRELOAD_INTERVAL)
        isPreloading = false
        startStage()
    }

    suspend fun startStage() {
        logMessageState.pushMessage(AnnotatedString("ステージ${currentStage.stage}開始"))
        currentStage.cards.forEach { card ->
            applyOneTimeVisualEffects(
                card = card,
                effect = VisualEffects.Appear(),
                awaitCompletion = false
            )
        }
        startTurn()
    }

    suspend fun startTurn() {
        lockScreen()
        currentStage.incrementTurn()
        currentPlayer.clearFlippedCards()
        logMessageState.pushMessage(AnnotatedString("ターン ${currentStage.turns}"))

        // 場札の効果発動
        dispatchAllEffectHandlersFromStage(handler = { it.onTurnStart() }, dispatcher = {
            it.dispatch(
                param = OnTurnStartEffectHandler.Param(
                    stageState = currentStage,
                    nextPlayer = currentPlayer
                )
            )
        })

        // 獲得済み能力の効果発動
        dispatchAllEffectHandlersFromPlayer(handler = { it.onTurnStart() }, dispatcher = {
            it.dispatch(
                param = OnTurnStartEffectHandler.Param(
                    stageState = currentStage,
                    nextPlayer = currentPlayer
                )
            )
        })

        // 能力によって表になったカードの処理
        handleAdditionalFlippedCard { unlockScreen() }
    }

    private suspend fun handleAdditionalFlippedCard(completion: () -> Unit) {
        val card = additionalFlippedCards.removeFirstOrNull()
        if (card == null) {
            completion()
        } else {
            flipCard(card = card)
        }
    }

    private fun lockScreen() {
        currentStage.disableAllCards()
        isButtonsEnable = false
    }

    private fun unlockScreen() {
        currentStage.enableAllCards()
        isButtonsEnable = true
    }

    fun onEndTurnClick() {
        // 連打抑止のため、同期的にUIを無効化
        lockScreen()
        viewModelScope.launch { 
            val shouldEnd = if (currentPlayer.isFlippable) {
                Dialogs.ConfirmEndTurn { dialogs.remove(it) }
                    .also { dialogs.add(it) }
                    .result.await()
            } else {
                true
            }
            if (shouldEnd) {
                endAndStartNextTurn()
            } else {
                unlockScreen()
            }
        }
    }

    fun onAutoClick() {
        // TODO 確認ダイアログ表示
        lockScreen()
        viewModelScope.launch {
            while (true) {
                val card = currentStage.cards.filter { !it.isFaceUp && !it.isMatched }
                    .randomOrNull()?.takeIf { currentPlayer.isFlippable }
                if (card == null) {
                    break
                } else {
                    currentPlayer.addFlippedCard(card = card)
                    flipCard(card)
                }
            }
            unlockScreen()
        }
    }

    private suspend fun endAndStartNextTurn() {
        // ステージ効果発動
        // 獲得済み能力の効果発動
        dispatchAllEffectHandlersFromPlayer(
            handler = { it.onTurnEnd() },
            dispatcher = {
                it.dispatch(
                    param = OnTurnEndEffectHandler.Param(
                        stageState = currentStage,
                        player = currentPlayer
                    )
                )
            }
        )
        // TODO 表のままにしておくカードの制御
        val waiters = currentStage.cards.filter { !it.isMatched && it.isFaceUp }.map { card ->
            viewModelScope.launch {
                applyOneTimeVisualEffects(
                    card = card,
                    effect = VisualEffects.Flip(
                        isInitialFaceUp = true,
                        onFaceChange = { card.changeSurface(isFaceUp = false) }
                    ),
                    awaitCompletion = true
                )
            }
        }
        waiters.joinAll()
        // delay(Constants.FLIP_ANIMATION_DURATION_MILLIS.toLong())
        startTurn()
    }

    private suspend fun flipCardByPlayer(card: AbilityCard) {
        // プレイヤー操作によってめくられたカードとして処理
        currentPlayer.addFlippedCard(card = card)
        flipCard(card = card) { unlockScreen() }
    }

    private suspend fun flipCard(card: AbilityCard, completion: () -> Unit = {}) {
        applyOneTimeVisualEffects(
            card = card,
            effect = VisualEffects.Flip(
                isInitialFaceUp = false,
                onFaceChange = { card.changeSurface(isFaceUp = true) }
            ),
            awaitCompletion = true
        )
        logMessageState.pushMessage(LogMessage(card.displayName, LogMessages.CARD_FLIPPED))

        // TODO 自動拡大の設定を確認
        if (discoveredCards.add(card.displayName)) {
            val dialog = Dialogs.CardDetail(card = card) { dialogs.remove(it) }
            dialogs.add(dialog)
            dialog.result.join()
        }

        // カードをめくったとき(ペア判定前)の効果発動
        // めくられたカード側
        card.onCardFlip()?.let {
            it.dispatch(OnCardFlipEffectHandler.Param(this, card))
            delay(Constants.GAME_FLOW_INTERVAL_NORMAL)
        }
        // 獲得済み能力側
        dispatchAllEffectHandlersFromPlayer(
            handler = { it.onCardFlip() },
            dispatcher = {
                it.dispatch(param = OnCardFlipEffectHandler.Param(this, card))
            }
        )

        // ペア成立判定
        val matchedCard = currentPlayer.flippedCards.find {
            // 同種の別カードで、かつ未獲得のカードを探す
            card.displayName == it.displayName && card.instanceId != it.instanceId &&
                    !it.isMatched
        }
        if (matchedCard == null) {
            // TODO ペア不成立時の効果発動
            // card.onPairUnmatch
        } else {
            // 盤面から除去
            val waiters = mutableListOf<Job>()
            waiters.add(viewModelScope.launch {
                applyOneTimeVisualEffects(
                    card = card,
                    effect = VisualEffects.Disappear { card.onMatch() },
                    awaitCompletion = true
                )
            })
            waiters.add(viewModelScope.launch {
                applyOneTimeVisualEffects(
                    card = matchedCard,
                    effect = VisualEffects.Disappear { matchedCard.onMatch() },
                    awaitCompletion = true
                )
            })
            waiters.joinAll()
            // currentStage.onPairMatch(card = card, matchedCard = matchedCard)
            logMessageState.pushMessage(LogMessage(card.displayName, LogMessages.CARD_MATCHED))
            // delay(Constants.GAME_FLOW_INTERVAL_NORMAL)

            // ペア成立時の効果発動
            // TODO 場札側
            // 獲得済み能力側
            dispatchAllEffectHandlersFromPlayer(
                handler = { it.onPairMatch() },
                dispatcher = {
                    it.dispatch(
                        param = OnPairMatchEffectHandler.Param(
                            player = currentPlayer,
                            flippedCard = card,
                            matchedCard = matchedCard
                        )
                    )
                }
            )

            // 能力獲得
            val ability = card.onEarn()
            currentPlayer.addAbility(ability = ability)
            delay(Constants.GAME_FLOW_INTERVAL_NORMAL)

            // 能力獲得時の効果発動
            // 獲得した能力
            ability.onEarn()?.let { handler ->
                dispatchEffectHandler(
                    handler,
                    dispatcher = { it.dispatch(param = OnAbilityEarnEffectHandler.Param(earnedPlayer = currentPlayer)) }
                )
            }
            // TODO 獲得前に持っていた能力
            // currentPlayer.earnedAbilities.mapNotNull {
            //     it.takeUnless { a -> a.instanceId == ability.instanceId }.onAnotherAbilityEarn()
            // }.forEach { ... }
        }
        // 一連の処理内で発動した効果によって新たに表になったカードがあれば、そのペア成立判定
        handleAdditionalFlippedCard(completion = completion)
    }

    fun onCardClick(card: AbilityCard) {
        // TODO ダブルクリックでめくる設定がONなら、それっぽい表示に
        if (card.isFaceUp) {
            dialogs.add(Dialogs.CardDetail(card = card) { dialogs.remove(it) })
        } else if (currentPlayer.isFlippable) {
            // 連打抑止のため、同期的にUIを無効化
            lockScreen()
            viewModelScope.launch {
                flipCardByPlayer(card = card)
            }
        } else {
            logMessageState.pushMessageAsync(AnnotatedString("このターンは、もうめくれない"))
        }
    }

    private suspend fun <T : EffectHandler, R : EffectHandler.Result> dispatchAllEffectHandlersFromStage(
        stage: StageState = currentStage,
        handler: (AbilityCard) -> T?,
        dispatcher: (T) -> R,
        action: suspend (R) -> Unit = {}
    ) {
        dispatchAllEffectHandlers(
            handlers = stage.cards.mapNotNull { handler(it) },
            dispatcher = dispatcher,
            action = action
        )
    }

    private suspend fun <T : EffectHandler, R : EffectHandler.Result> dispatchAllEffectHandlersFromPlayer(
        player: PlayerState = currentPlayer,
        handler: (EarnedAbility) -> T?,
        dispatcher: suspend (T) -> R,
        action: suspend (R) -> Unit = {}
    ) {
        dispatchAllEffectHandlers(
            handlers = player.earnedAbilities.mapNotNull { handler(it) },
            dispatcher = dispatcher,
            action = action
        )
    }

    private suspend fun <T : EffectHandler, R : EffectHandler.Result> dispatchAllEffectHandlers(
        handlers: Collection<T>,
        dispatcher: suspend (T) -> R,
        action: suspend (R) -> Unit = {}
    ) {
        handlers.sortedBy { it.priority }.forEach { handler ->
            dispatchEffectHandler(handler, dispatcher, action)
        }
    }

    private suspend fun <T : EffectHandler, R : EffectHandler.Result> dispatchEffectHandler(
        handler: T,
        dispatcher: suspend (T) -> R,
        action: suspend (R) -> Unit = {}
    ) {
        val result = dispatcher(handler).also {
            it.message?.let { message -> logMessageState.pushMessageAsync(message = message) }
            it.gainedEffects.forEach { effect -> currentPlayer.onEffectGain(effect = effect) }
            it.lostEffectParentInstanceIds.forEach { instanceId ->
                currentPlayer.onEffectLost(parentAbilityInstanceId = instanceId)
            }
            additionalFlippedCards.addAll(it.additionalFlippedCards)
        }
        action(result)
        delay(Constants.GAME_FLOW_INTERVAL_NORMAL)
    }

    suspend fun applyOneTimeVisualEffects(
        card: AbilityCard,
        effect: VisualEffects,
        awaitCompletion: Boolean
    ) {
        effect.addCallback { card.removeVisualEffects(effect) }
        if (awaitCompletion) {
            val job = Job()
            effect.addCallback { job.complete() }
            card.applyVisualEffects(effect)
            job.join()
        } else {
            card.applyVisualEffects(effect)
        }
    }
}

@Composable
fun gameStateViewModel(initialStage: Int = 1, playerCount: Int = 1) = viewModel {
    GameStateViewModel(initialStage, playerCount)
}