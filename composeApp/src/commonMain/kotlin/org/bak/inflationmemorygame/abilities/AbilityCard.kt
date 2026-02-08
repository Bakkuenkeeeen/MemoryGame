package org.bak.inflationmemorygame.abilities

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.components.VisualEffects
import kotlin.random.Random

@Stable
abstract class AbilityCard(
    ability: Abilities,
    val earnedAbilityFactory: () -> EarnedAbility
) : Ability by ability {

    /** 盤面上の同一の能力を区別するためのID. */
    val instanceId: Long = Random.nextLong()

    /** 表になっているかどうか. */
    var isFaceUp: Boolean by mutableStateOf(false)
        private set

    var isMatched: Boolean by mutableStateOf(false)
        private set

    /** 「虚無」化しているかどうか. */
    var isVoidOut: Boolean by mutableStateOf(false)
        private set

    /** クリック可能かどうか. */
    var isInteractionEnabled: Boolean by mutableStateOf(true)

    val visualEffects = mutableStateListOf<VisualEffects>()

    fun changeSurface(isFaceUp: Boolean) {
        this.isFaceUp = isFaceUp
    }

    fun onMatch() {
        isMatched = true
    }

    fun applyVisualEffects(effect: VisualEffects) {
        visualEffects.add(effect)
    }

    fun removeVisualEffects(effect: VisualEffects) {
        visualEffects.remove(effect)
    }

    abstract fun onTurnStart(): OnTurnStartEffectHandler?
    abstract fun onCardFlip(): OnCardFlipEffectHandler?

    abstract class NoFieldEffect(ability: Abilities, earnedAbilityFactory: () -> EarnedAbility) :
        AbilityCard(ability = ability, earnedAbilityFactory = earnedAbilityFactory) {
        override fun onTurnStart(): OnTurnStartEffectHandler? = null
        override fun onCardFlip(): OnCardFlipEffectHandler? = null
    }
}