package org.bak.inflationmemorygame.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.StatusEffect
import org.bak.inflationmemorygame.params.Params

@Stable
class PlayerState {

    /** 獲得済み能力リスト. */
    val earnedAbilities = mutableStateListOf<EarnedAbility>()

    /** 発動中の効果リスト. */
    private val statusEffects = mutableStateListOf<StatusEffect>()

    /** めくれる枚数上限. */
    val maxFlipCount: Int by derivedStateOf {
        val fixedValueEffects =
            statusEffects.filter { it.calculationType is StatusEffect.CalculationType.Fixed }
        if (fixedValueEffects.isEmpty()) {
            Params.DEFAULT_FLIP_COUNT + statusEffects.sumOf { it.amount }
        } else {
            fixedValueEffects.maxByOrNull {
                (it.calculationType as StatusEffect.CalculationType.Fixed).priority
            }!!.amount
        }
    }

    /** めくったカードリスト. */
    val flippedCards = mutableStateListOf<AbilityCard>()

    /** めくった枚数. */
    val flippedCount: Int by derivedStateOf { flippedCards.size }

    /** まだめくれるか. */
    val isFlippable: Boolean by derivedStateOf { flippedCount < maxFlipCount }

    fun onFlip(card: AbilityCard) {
        flippedCards.add(card)
    }

    fun onAbilityEarn(ability: EarnedAbility) {
        earnedAbilities.add(ability)
    }

    fun onEffectGain(effect: StatusEffect) {
        statusEffects.add(effect)
    }

    fun onTurnStart() {
        flippedCards.clear()
    }
}

@Composable
fun rememberPlayerState(): PlayerState {
    return PlayerState()
}