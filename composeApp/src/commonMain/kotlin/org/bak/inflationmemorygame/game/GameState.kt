package org.bak.inflationmemorygame.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler

@Stable
class GameState(
    private val stages: SnapshotStateList<StageState>,
    private val players: SnapshotStateList<PlayerState>
) {

    val isStageReady: Boolean by derivedStateOf { currentStage.cards.isNotEmpty() }

    val currentStage: StageState by derivedStateOf { stages.maxBy { it.stage } }

    val currentPlayer: PlayerState by derivedStateOf {
        players[(currentStage.turns - 1) % players.size]
    }

    fun startStage() {

    }

    fun startTurn() {
        currentStage.also { it.onTurnStart() }
            .cards.mapNotNull { it?.onTurnStart() }.forEach {
                it.dispatch(param = OnTurnStartEffectHandler.Param(gameState = this))
            }
        currentPlayer.also { it.onTurnStart() }
            .earnedAbilities.mapNotNull { it.onTurnStart() }.forEach {
                it.dispatch(param = OnTurnStartEffectHandler.Param(gameState = this))
            }
    }

    fun onCardClick(card: AbilityCard) {
        // TODO ダブルクリックでめくる設定がONなら、それっぽい表示に
        if (card.isFaceUp) {
            // TODO 拡大表示
        } else {
            currentStage.onFlip(card = card)
            currentPlayer.onFlip(card = card)
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
fun rememberSinglePlayerGameState(
    stageState: StageState = rememberStageState(),
    playerState: PlayerState = rememberPlayerState()
): GameState {
    return GameState(
        stages = mutableStateListOf(stageState),
        players = mutableStateListOf(playerState)
    )
}