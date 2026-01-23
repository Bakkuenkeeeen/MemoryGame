package org.bak.inflationmemorygame.abilities

import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
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
    abstract fun onTurnEnd(): OnTurnEndEffectHandler?
    open fun onLost(): OnAbilityLostEffectHandler? {
        if (effectState == EffectState.Active) {
            return object : OnAbilityLostEffectHandler {
                // TODO 複数の能力が一気に削除された場合に、削除される順番を考慮しないといけないケースが出てくる？
                override val priority: Int = EffectHandler.PRIORITY_DEFAULT
                override fun dispatch(param: OnAbilityLostEffectHandler.Param): EffectHandler.Result {
                    param.owner.onAbilityLost(ability = this@EarnedAbility)
                    return EffectHandler.Result(
                        abilityName = displayName,
                        lostEffectParentInstanceId = instanceId
                    )
                }
            }
        }
        return null
    }

    protected enum class EffectState {
        Idle,
        Ready,
        Active,
        End
    }
}