package org.bak.inflationmemorygame.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.bak.inflationmemorygame.Settings
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.abilities.EffectHandlerResults
import org.bak.inflationmemorygame.abilities.GrowAbility
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandlerParam
import org.bak.inflationmemorygame.components.VisualEffects
import org.bak.inflationmemorygame.dialogs.DialogState
import org.bak.inflationmemorygame.dialogs.Dialogs
import org.bak.inflationmemorygame.isPreloadNeeded
import org.bak.inflationmemorygame.loadSettings
import org.bak.inflationmemorygame.logs.LogState
import org.bak.inflationmemorygame.logs.Logs
import org.bak.inflationmemorygame.values.Constants

class GameStateViewModel(
    initialStage: Int,
    playerCount: Int,
    shouldPreload: Boolean = isPreloadNeeded(),
    private val settings: Settings = loadSettings()
) : ViewModel(),
    LogState by LogState(),
    DialogState by DialogState() {

    private val stages = mutableStateListOf(StageState(stage = initialStage))
    private val players = mutableStateListOf<PlayerState>().apply {
        addAll(List(playerCount) { PlayerState() })
    }

    val currentStage: StageState by derivedStateOf { stages.maxBy { it.stage } }

    val currentPlayer: PlayerState by derivedStateOf {
        players[(currentStage.turns - 1) % players.size]
    }

    var isButtonsEnable: Boolean by mutableStateOf(true)
        private set

    private val additionalFlippedCards = mutableListOf<AbilityCard>()

    var isPreloading by mutableStateOf(shouldPreload)
        private set

    private val discoveredCards = mutableSetOf<String>()

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
        // 溜まったダイアログのスタックをクリア
        clearDialogs()
        appendLog(log = Logs.StartState(currentStage.stage))
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
        appendLog(log = Logs.StartTurn(currentStage.turns))

        // 場札の効果発動
        dispatchAllEffectHandlersFromStage(
            handler = { it.onTurnStart() },
            param = OnTurnStartEffectHandlerParam(
                stageState = currentStage,
                nextPlayer = currentPlayer
            )
        )

        // 獲得済み能力の効果発動
        dispatchAllEffectHandlersFromPlayer(
            handler = { it.onTurnStart() },
            param = OnTurnStartEffectHandlerParam(
                stageState = currentStage,
                nextPlayer = currentPlayer
            )
        )

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
                showDialog(Dialogs.ConfirmEndTurn()).isConfirmed
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
        lockScreen()
        viewModelScope.launch {
            if (currentPlayer.isFlippable) {
                val result = if (settings.isConfirmAutoFlipSkip) {
                    null
                } else {
                    showDialog(Dialogs.ConfirmAutoFlip())
                }
                if (result?.isConfirmed != false) {
                    if (result?.skipForever == true) {
                        settings.update(isConfirmAutoFlipSkip = true)
                    }
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
                }
            } else {
                onNotFlippable()
            }
            unlockScreen()
        }
    }

    private suspend fun endAndStartNextTurn() {
        // ステージ効果発動
        // 獲得済み能力の効果発動
        dispatchAllEffectHandlersFromPlayer(
            handler = { it.onTurnEnd() },
            param = OnTurnEndEffectHandlerParam(
                stageState = currentStage,
                player = currentPlayer
            )
        )
        val waiters = currentStage.cards.filter {
            !it.isMatched && it.isFaceUp &&
                    keepFlippedCards.all { keep -> keep.instanceId != it.instanceId }
        }.map { card ->
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
        keepFlippedCards.clear()
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
        appendLog(log = Logs.Discover(card.displayName))

        if (discoveredCards.add(card.displayName)) {
            if (settings.shouldShowDetailWhenDiscover) {
                showDialog(Dialogs.CardDetail(card = card))
            }
        }

        // カードをめくったとき(ペア判定前)の効果発動
        // めくられたカード側
        card.onCardFlip()?.let {
            it.dispatch(OnCardFlipEffectHandlerParam(this, card))
            delay(Constants.GAME_FLOW_INTERVAL_NORMAL)
        }
        // 獲得済み能力側
        dispatchAllEffectHandlersFromPlayer(
            handler = { it.onCardFlip() },
            param = OnCardFlipEffectHandlerParam(this, card)
        )

        // ペア成立判定
        val matchedCard = currentStage.findMatchedCard(card = card)
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
            // delay(Constants.GAME_FLOW_INTERVAL_NORMAL)

            // ペア成立時の効果発動
            // TODO 場札側
            // 獲得済み能力側
            dispatchAllEffectHandlersFromPlayer(
                handler = { it.onPairMatch() },
                param = OnPairMatchEffectHandlerParam(
                    player = currentPlayer,
                    flippedCard = card,
                    matchedCard = matchedCard
                )
            )

            // 能力獲得orレベルアップ
            val earned = currentPlayer.earnedAbilities.find { it.displayName == card.displayName }
            val ability = if (earned is GrowAbility) {
                earned.also {
                    it.levelUp()
                    appendLog(log = Logs.LevelUp(card.displayName))
                }
            } else {
                card.earnedAbilityFactory().also {
                    currentPlayer.addAbility(ability = it)
                    appendLog(log = Logs.Match(card.displayName))
                }
            }
            delay(Constants.GAME_FLOW_INTERVAL_NORMAL)

            // 能力獲得時の効果発動
            // 獲得した能力
            ability.onEarn()?.let { handler ->
                dispatchEffectHandler(
                    handler,
                    param = OnAbilityEarnEffectHandlerParam(earnedPlayer = currentPlayer)
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
        // 連打抑止のため、同期的にUIを無効化
        lockScreen()
        viewModelScope.launch {
            // TODO ダブルクリックでめくる設定がONなら、それっぽい表示に
            if (card.isFaceUp) {
                showDialog(Dialogs.CardDetail(card = card))
                unlockScreen()
            } else if (currentPlayer.isFlippable) {
                flipCardByPlayer(card = card)
            } else {
                onNotFlippable()
                unlockScreen()
            }
        }
    }

    private suspend fun onNotFlippable() {
        appendLog(log = Logs.NotFlippable())
    }

    private suspend fun <TParam : EffectHandler.Param> dispatchAllEffectHandlersFromStage(
        stage: StageState = currentStage,
        handler: (AbilityCard) -> EffectHandler<TParam>?,
        param: TParam
    ) {
        dispatchAllEffectHandlers(
            handlers = stage.cards.mapNotNull { handler(it) },
            param = param
        )
    }

    private suspend fun <TParam : EffectHandler.Param> dispatchAllEffectHandlersFromPlayer(
        player: PlayerState = currentPlayer,
        handler: (EarnedAbility) -> EffectHandler<TParam>?,
        param: TParam
    ) {
        dispatchAllEffectHandlers(
            handlers = player.earnedAbilities.mapNotNull { handler(it) },
            param = param
        )
    }

    private suspend fun <TParam : EffectHandler.Param> dispatchAllEffectHandlers(
        handlers: Collection<EffectHandler<TParam>>,
        param: TParam
    ) {
        handlers.sortedBy { it.priority }.forEach { handler ->
            dispatchEffectHandler(handler = handler, param = param)
        }
    }

    private suspend fun <TParam : EffectHandler.Param> dispatchEffectHandler(
        handler: EffectHandler<TParam>,
        param: TParam
    ) {
        val results = handler.dispatch(param = param)
        results.forEach { result ->
            when (result) {
                is EffectHandlerResults.PrintLog -> {
                    appendLog(result.log)
                }

                is EffectHandlerResults.GainStatusEffect -> {
                    currentPlayer.onEffectGain(result.effect)
                }

                is EffectHandlerResults.LostStatusEffect -> {
                    currentPlayer.onEffectLost(result.parentInstanceId)
                }

                is EffectHandlerResults.ApplyVisualEffect -> {
                    currentStage.cards.find { it.instanceId == result.targetInstanceId }?.let {
                        applyOneTimeVisualEffects(
                            card = it,
                            effect = result.visualEffect,
                            awaitCompletion = result.awaitCompletion
                        )
                    }
                }

                is EffectHandlerResults.KeepFlipped -> {
                    currentStage.cards.find { it.instanceId == result.targetInstanceId }?.let {
                        keepFlippedCards.add(it)
                    }
                }

                is EffectHandlerResults.ReverseCard -> {
                    currentStage.cards.find { it.instanceId == result.targetInstanceId }
                        ?.changeSurface(isFaceUp = false)
                }
            }
        }
    }

    private val keepFlippedCards = mutableListOf<AbilityCard>()

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