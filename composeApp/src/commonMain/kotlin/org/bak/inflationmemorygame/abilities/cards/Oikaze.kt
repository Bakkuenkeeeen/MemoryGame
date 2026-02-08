package org.bak.inflationmemorygame.abilities.cards

import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.EffectHandlerResults
import org.bak.inflationmemorygame.abilities.StatusEffect
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.logs.Logs
import org.bak.inflationmemorygame.values.Params

class OikazeCard : AbilityCard.NoFieldEffect(
    ability = Abilities.Oikaze,
    earnedAbilityFactory = ::OikazeAbility
)

class OikazeAbility : EarnedAbility(ability = Abilities.Oikaze) {
    override fun onEarn(): OnAbilityEarnEffectHandler? {
        changeEffectState(to = EffectState.Ready)
        return null
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onCardFlip(): OnCardFlipEffectHandler? = null

    override fun onPairMatch(): OnPairMatchEffectHandler? {
        if (tryChangeEffectState(from = EffectState.Ready, to = EffectState.Active)) {
            return object : OnPairMatchEffectHandler {
                override val priority: Int = OnPairMatchEffectHandler.PROIORITY_OIKAZE
                override suspend fun dispatch(param: OnPairMatchEffectHandlerParam): List<EffectHandlerResults> {
                    return EffectHandlerResults.GainStatusEffect(
                        effect = StatusEffect(
                            parentAbilityInstanceId = instanceId,
                            amount = Params.OIKAZE_AMOUNT,
                            calculationType = StatusEffect.CalculationType.Add
                        )
                    ).withLog(Logs.EffectActivate(name = displayName))
                }
            }
        }
        return null
    }

    override fun onTurnEnd(): OnTurnEndEffectHandler? {
        if (tryChangeEffectState(from = EffectState.Active, to = EffectState.Ready)) {
            // このターン内に発動した効果をリセット
            return object : OnTurnEndEffectHandler {
                override val priority: Int = OnTurnEndEffectHandler.PRIORITY_OIKAZE
                override suspend fun dispatch(param: OnTurnEndEffectHandlerParam): List<EffectHandlerResults> {
                    return EffectHandlerResults.LostStatusEffect(parentInstanceId = instanceId)
                        .withLog(Logs.EffectDeactivate(name = displayName))
                }
            }
        } else {
            return null
        }
    }
}