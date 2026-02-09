package org.bak.inflationmemorygame.abilities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnLevelUpEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.logs.Logs
import kotlin.random.Random

abstract class EarnedAbility(ability: Abilities) : Ability.Implementable by ability {

    /** 同一の獲得済み能力を区別するためのID. */
    val instanceId: Long = Random.nextLong()

    override var level: Int by mutableIntStateOf(1)
    open fun tryLevelUp(matchedAbility: Ability, amount: Int = 1): OnLevelUpEffectHandler? {
        if (id == matchedAbility.id && level + amount <= maxLevel) {
            level += amount
            return onLevelUp(amount = amount)
        }
        return null
    }

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
    protected abstract fun onLevelUp(amount: Int): OnLevelUpEffectHandler

    abstract fun onTurnStart(): OnTurnStartEffectHandler?

    abstract fun onCardFlip(): OnCardFlipEffectHandler?

    abstract fun onPairMatch(): OnPairMatchEffectHandler?

    abstract fun onTurnEnd(): OnTurnEndEffectHandler?

    open fun onLost(): OnAbilityLostEffectHandler? {
        if (level > 1) {
            level--
            return onLevelDown()
        }
        if (effectState == EffectState.Active) {
            return object : OnAbilityLostEffectHandler {
                // TODO 複数の能力が一気に削除された場合に、削除される順番を考慮しないといけないケースが出てくる？
                override val priority: Int = OnAbilityLostEffectHandler.PRIORITY_DEFAULT
                override suspend fun dispatch(param: OnAbilityLostEffectHandlerParam) =
                    buildEffectHandlerResults {
                        printLog(Logs.lostStatusEffect { displayName })
                        lostStatusEffect(parentInstanceId = instanceId)
                    }
            }
        }
        return null
    }

    protected abstract fun onLevelDown(): OnAbilityLostEffectHandler?

    protected enum class EffectState {
        Idle,
        Ready,
        Active,
        End
    }
}