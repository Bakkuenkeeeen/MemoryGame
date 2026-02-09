package org.bak.inflationmemorygame.abilities.cards

import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.GainAbilityError
import org.bak.inflationmemorygame.abilities.StatusEffect
import org.bak.inflationmemorygame.abilities.buildEffectHandlerResults
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.logs.Logs
import org.bak.inflationmemorygame.values.Params
import kotlin.math.max
import kotlin.math.min

class TotteokiCard : AbilityCard(ability = Abilities.Totteoki) {
    companion object {
        // 表のまま残ったカード側のgainAbilityが呼ばれても、
        // 後からめくったカード側のgainAbilityが呼ばれてもいいように、
        // 蓄積数はインスタンスフィールドでは持たないようにする
        private var turns = -1
    }

    override val description: String
        get() = if (turns == -1) {
            super.description
        } else {
            super.description + "\n\n現在の蓄積数 : ${max(0, turns)}"
        }

    override fun gainAbility(): Result<EarnedAbility> {
        val amount = turns
        turns = -1
        return if (amount > 0) {
            Result.success(TotteokiAbility(amount))
        } else {
            Result.failure(GainAbilityError.TotteokiMatchTooEarlyError())
        }
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onCardFlip(): OnCardFlipEffectHandler? = null
    override fun onTurnEnd(): OnTurnEndEffectHandler? {
        return if (isFaceUp && !isMatched) {
            turns = min(turns + 1, Params.TOTTEOKI_MAX)
            object : OnTurnEndEffectHandler {
                override val priority: Int = OnTurnEndEffectHandler.PRIORITY_FIELD_TOTTEOKI
                override suspend fun dispatch(param: OnTurnEndEffectHandlerParam) =
                    buildEffectHandlerResults {
                        keepFlipped(targetInstanceId = instanceId)
                    }
            }
        } else {
            null
        }
    }
}

class TotteokiAbility(private val amount: Int) : EarnedAbility(ability = Abilities.Totteoki) {
    override fun onEarn(): OnAbilityEarnEffectHandler {
        changeEffectState(to = EffectState.Active)
        return object : OnAbilityEarnEffectHandler {
            override val priority: Int = OnAbilityEarnEffectHandler.PRIORITY_TOTTEOKI
            override suspend fun dispatch(param: OnAbilityEarnEffectHandlerParam) =
                buildEffectHandlerResults {
                    printLog(Logs.gainStatusEffect(name = displayName))
                    gainStatusEffect(
                        effect = StatusEffect(
                            parentAbilityInstanceId = instanceId,
                            amount = amount,
                            calculationType = StatusEffect.CalculationType.Add
                        )
                    )
                }
        }
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onCardFlip(): OnCardFlipEffectHandler? = null
    override fun onPairMatch(): OnPairMatchEffectHandler? = null
    override fun onTurnEnd(): OnTurnEndEffectHandler? = null
}