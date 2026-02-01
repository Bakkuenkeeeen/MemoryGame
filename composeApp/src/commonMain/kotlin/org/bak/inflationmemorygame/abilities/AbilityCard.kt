package org.bak.inflationmemorygame.abilities

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.values.Constants
import kotlin.random.Random

@Stable
abstract class AbilityCard(private val actual: Abilities) : Ability by actual {

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

    var visualEffect: VisualEffects? by mutableStateOf(null)
        private set

    fun changeSurface(isFaceUp: Boolean) {
        this.isFaceUp = isFaceUp
    }

    fun onMatch() {
        isMatched = true
    }

    fun applyVisualEffects(effect: VisualEffects) {
        visualEffect = effect
    }

    fun removeVisualEffects() {
        visualEffect = null
    }

    abstract fun onEarn(): EarnedAbility
    abstract fun onTurnStart(): OnTurnStartEffectHandler?
    abstract fun onCardFlip(): OnCardFlipEffectHandler?

    sealed class VisualEffects {
        data object Flash : VisualEffects()
    }

    abstract class NoFieldEffect(actual: Abilities): AbilityCard(actual = actual) {
        override fun onTurnStart(): OnTurnStartEffectHandler? = null
        override fun onCardFlip(): OnCardFlipEffectHandler? = null
    }
}