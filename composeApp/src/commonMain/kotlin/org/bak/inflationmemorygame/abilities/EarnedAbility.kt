package org.bak.inflationmemorygame.abilities

import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import kotlin.random.Random

abstract class EarnedAbility(val actual: Abilities) : Ability by actual {

    /** 同一の獲得済み能力を区別するためのID. */
    val instanceId: Long = Random.nextLong()

    protected var effectState: EffectState = EffectState.Idle
        private set

    protected fun tryChangeEffectState(from: EffectState, to: EffectState): Boolean {
        if (effectState == from) {
            effectState = to
            return true
        }
        return false
    }

    abstract fun onEarn(): OnAbilityEarnEffectHandler?
    abstract fun onTurnStart(): OnTurnStartEffectHandler?
    abstract fun onLost(): OnAbilityLostEffectHandler?

    protected enum class EffectState {
        Idle,
        Ready,
        Active,
        End
    }
}