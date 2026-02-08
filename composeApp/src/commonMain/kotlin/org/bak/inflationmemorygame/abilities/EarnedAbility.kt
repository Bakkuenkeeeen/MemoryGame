package org.bak.inflationmemorygame.abilities

import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import kotlin.random.Random

abstract class EarnedAbility(ability: Abilities) : Ability by ability {

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

    protected fun changeEffectState(to: EffectState) {
        effectState = to
    }

    abstract fun onEarn(): OnAbilityEarnEffectHandler?

    abstract fun onTurnStart(): OnTurnStartEffectHandler?

    abstract fun onCardFlip(): OnCardFlipEffectHandler?

    abstract fun onPairMatch(): OnPairMatchEffectHandler?

    abstract fun onTurnEnd(): OnTurnEndEffectHandler?

    open fun onLost(): OnAbilityLostEffectHandler? {
        if (effectState == EffectState.Active) {
            return object : OnAbilityLostEffectHandler {
                // TODO 複数の能力が一気に削除された場合に、削除される順番を考慮しないといけないケースが出てくる？
                override val priority: Int = OnAbilityLostEffectHandler.PRIORITY_DEFAULT
                override suspend fun dispatch(param: OnAbilityLostEffectHandlerParam): List<EffectHandlerResults> {
                    return EffectHandlerResults.PrintLog.onAbilityLost(name = displayName) +
                            EffectHandlerResults.LostStatusEffect(parentInstanceId = instanceId)
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