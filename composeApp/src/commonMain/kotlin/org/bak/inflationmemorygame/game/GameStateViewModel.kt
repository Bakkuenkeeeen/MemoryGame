package org.bak.inflationmemorygame.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.values.LogMessages
import org.bak.inflationmemorygame.values.Params

class GameStateViewModel(initialStage: Int, playerCount: Int) : ViewModel() {

    private val stages = mutableStateListOf(StageState(stage = initialStage))
    private val players = mutableStateListOf<PlayerState>().apply {
        addAll(List(playerCount) { PlayerState() })
    }

    val currentStage: StageState by derivedStateOf { stages.maxBy { it.stage } }

    val currentPlayer: PlayerState by derivedStateOf {
        players[(currentStage.turns - 1) % players.size]
    }

    val logMessageState = LogMessageState(coroutineScope = viewModelScope)

    private val additionalFlippedCards = mutableListOf<AbilityCard>()

    init {
        viewModelScope.launch { startStage() }
    }

    suspend fun startStage() {
        delay(3_000)
        if (currentStage.tryStartStage()) {
            logMessageState.pushMessage(AnnotatedString("ステージ${currentStage.stage}開始"))
        } else {
            logMessageState.pushMessage(AnnotatedString("すでにステージが生成されています"))
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
        // TODO ボタンも無効化
    }

    private fun unlockScreen() {
        currentStage.enableAllCards()
        // TODO ボタンも有効化
    }

    fun onEndTurnClick() {
        if (currentPlayer.isFlippable) {
            // TODO ダイアログ表示
        } else {
            // 連打抑止のため、同期的にUIを無効化
            lockScreen()
            endTurnAsync()
        }
    }

    private fun endTurnAsync() {
        viewModelScope.launch {
            // ステージ効果発動
            // 獲得済み能力の効果発動
            // TODO 表のままにしておくカードの制御
            currentStage.reverseAllCards()
            delay(Params.CARD_FLIP_ANIMATION_DURATION_MILLIS.toLong())
            startTurn()
        }
    }

    private fun flipCardByUserAsync(card: AbilityCard) {
        viewModelScope.launch {
            // プレイヤー操作によってめくられたカードとして処理
            currentPlayer.addFlippedCard(card = card)
            flipCard(card = card)
        }
    }

    private suspend fun flipCard(card: AbilityCard) {
        currentStage.flipCard(card = card)
        logMessageState.pushMessage(LogMessage(card.displayName, LogMessages.CARD_FLIPPED))

        // TODO 自動拡大の設定がONなら、ダイアログ出してから

        // カードをめくったとき(ペア判定前)の効果発動
        // めくられたカード側
        card.onCardFlip()?.dispatch(OnCardFlipEffectHandler.Param())
        // TODO 獲得済み能力側
        // currentPlayer.earnedAbilities.mapNotNull { it.onCardFlip() }.forEach {
        //     it.dispatch(param = OnCardFlipEffectHandler.Param())
        // }

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
            currentStage.onPairMatch(card = card, matchedCard = matchedCard)
            // TODO ペア成立時の効果発動

            // 能力獲得
            val ability = card.onEarn()
            currentPlayer.onAbilityEarn(ability = ability)
            // 能力獲得時の効果発動
            // 獲得した能力
            ability.onEarn()?.let { handler ->
                dispatchEffectHandler(
                    handler,
                    dispatcher = {
                        it.dispatch(
                            param = OnAbilityEarnEffectHandler.Param(earnedPlayer = currentPlayer)
                        )
                    }
                )
            }
            // TODO 獲得前に持っていた能力
            // currentPlayer.earnedAbilities.mapNotNull {
            //     it.takeUnless { a -> a.instanceId == ability.instanceId }.onAnotherAbilityEarn()
            // }.forEach { ... }
        }
        // TODO 一連の処理内で発動した効果によって表になったカードのペア成立判定
        unlockScreen()
    }

    fun onCardClick(card: AbilityCard) {
        // TODO ダブルクリックでめくる設定がONなら、それっぽい表示に
        if (card.isFaceUp) {
            // TODO 拡大表示
        } else if (currentPlayer.isFlippable) {
            // 連打抑止のため、同期的にUIを無効化
            lockScreen()
            flipCardByUserAsync(card = card)
        } else {
            logMessageState.pushMessageAsync(AnnotatedString("このターンは、もうめくれない"))
        }
    }

    private fun <T : EffectHandler, R : EffectHandler.Result> dispatchAllEffectHandlersFromStage(
        stage: StageState = currentStage,
        handler: (AbilityCard) -> T?,
        dispatcher: (T) -> R,
        action: (R) -> Unit = {}
    ) {
        dispatchAllEffectHandlers(
            handlers = stage.cards.mapNotNull { handler(it) },
            dispatcher = dispatcher,
            action = action
        )
    }

    private fun <T : EffectHandler, R : EffectHandler.Result> dispatchAllEffectHandlersFromPlayer(
        player: PlayerState = currentPlayer,
        handler: (EarnedAbility) -> T?,
        dispatcher: (T) -> R,
        action: (R) -> Unit = {}
    ) {
        dispatchAllEffectHandlers(
            handlers = player.earnedAbilities.mapNotNull { handler(it) },
            dispatcher = dispatcher,
            action = action
        )
    }

    private fun <T : EffectHandler, R : EffectHandler.Result> dispatchAllEffectHandlers(
        handlers: Collection<T>,
        dispatcher: (T) -> R,
        action: (R) -> Unit = {}
    ) {
        handlers.sortedBy { it.priority }.forEach { handler ->
            dispatchEffectHandler(handler, dispatcher, action)
        }
    }

    private fun <T : EffectHandler, R : EffectHandler.Result> dispatchEffectHandler(
        handler: T,
        dispatcher: (T) -> R,
        action: (R) -> Unit = {}
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
    }
}

@Composable
fun gameStateViewModel(initialStage: Int = 1, playerCount: Int = 1) = viewModel {
    GameStateViewModel(initialStage, playerCount)
}