package org.bak.inflationmemorygame.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.params.Params

class GameStateViewModel(initialStage: Int, playerCount: Int) : ViewModel() {

    private val stages = mutableStateListOf<StageState>(StageState(stage = initialStage))
    private val players = mutableStateListOf<PlayerState>().apply {
        addAll(List(playerCount) { PlayerState() })
    }

    val currentStage: StageState by derivedStateOf { stages.maxBy { it.stage } }

    val currentPlayer: PlayerState by derivedStateOf {
        players[(currentStage.turns - 1) % players.size]
    }

    val messages = mutableStateListOf<LogMessage>()

    init {
        viewModelScope.launch { startStage() }
    }

    fun pushMessage(message: AnnotatedString) {
        messages.add(LogMessage(message = message))
        viewModelScope.launch {
            delay(Params.MESSAGE_SPEED_IN_MILLIS_NORMAL)
            messages.removeFirst()
        }
    }

    suspend fun startStage() {
        pushMessage(AnnotatedString("ステージ${currentStage.stage}を生成中..."))
        delay(5_000)
        if (currentStage.tryStartStage()) {
            pushMessage(AnnotatedString("ステージ${currentStage.stage}開始"))
        } else {
            pushMessage(AnnotatedString("すでにステージが生成されています"))
        }
        startTurn()
    }

    fun startTurn() {
        currentStage.also {
            it.onTurnStart()
            pushMessage(AnnotatedString("ターン ${currentStage.turns}"))
        }.cards.mapNotNull { it?.onTurnStart() }.forEach {
            // it.dispatch(param = OnTurnStartEffectHandler.Param(gameState = this))
        }
        currentPlayer.also { it.onTurnStart() }
            .earnedAbilities.mapNotNull { it.onTurnStart() }.forEach {
                // it.dispatch(param = OnTurnStartEffectHandler.Param(gameState = this))
            }
    }

    fun onCardClick(card: AbilityCard) {
        // TODO ダブルクリックでめくる設定がONなら、それっぽい表示に
        if (card.isFaceUp) {
            // TODO 拡大表示
        } else {
            currentStage.onFlip(card = card)
            currentPlayer.onFlip(card = card)
            pushMessage(buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(card.displayName)
                }
                append("を発見した")
            })
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
                    handler.dispatch(
                        param = OnAbilityEarnEffectHandler.Param(earnedPlayer = currentPlayer)
                    ).gainedEffect?.let { effect ->
                        currentPlayer.onEffectGain(effect = effect)
                    }
                }
                // TODO 獲得前に持っていた能力
                // currentPlayer.earnedAbilities.mapNotNull {
                //     it.takeUnless { a -> a.instanceId == ability.instanceId }.onAnotherAbilityEarn()
                // }.forEach { ... }
            }
            // TODO 一連の処理内で発動した効果によって表になったカードのペア成立判定
        }
    }
}

@Composable
fun gameStateViewModel(initialStage: Int = 1, playerCount: Int = 1) = viewModel {
    GameStateViewModel(initialStage, playerCount)
}