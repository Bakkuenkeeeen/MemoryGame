package org.bak.inflationmemorygame.abilities.cards

import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.abilities.StatusEffect
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler

class SuperhumanCard : AbilityCard(actual = Abilities.Superhuman) {
    override fun onEarn(): EarnedAbility {
        return SuperhumanAbility()
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onCardFlip(): OnCardFlipEffectHandler? = null
}

class SuperhumanAbility : EarnedAbility(actual = Abilities.Superhuman) {
    override fun onEarn(): OnAbilityEarnEffectHandler? {
        if (tryChangeEffectState(from = EffectState.Idle, to = EffectState.Ready)) {
            println("次のターンに発動")
        }
        return null
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? {
        if (tryChangeEffectState(from = EffectState.Ready, to = EffectState.Active)) {
            return object : OnTurnStartEffectHandler {
                override val priority: Int = OnTurnStartEffectHandler.PRIORITY_SUPERHUMAN
                override fun dispatch(param: OnTurnStartEffectHandler.Param): EffectHandler.Result {
                    return EffectHandler.Result(
                        abilityName = displayName,
                        gainedEffect = StatusEffect(
                            parentAbilityInstanceId = instanceId,
                            amount = param.nextPlayer.maxFlipCount,
                            calculationType = StatusEffect.CalculationType.Add
                        )
                    )
                }
            }
        }
        return null
    }

    override fun onTurnEnd(): OnTurnEndEffectHandler? {
        if (tryChangeEffectState(from = EffectState.Active, to = EffectState.End)) {
            return object : OnTurnEndEffectHandler {
                override val priority: Int = EffectHandler.PRIORITY_DEFAULT
                override fun dispatch(param: OnTurnEndEffectHandler.Param): EffectHandler.Result {
                    return EffectHandler.Result(
                        abilityName = displayName,
                        lostEffectParentInstanceId = instanceId
                    )
                }
            }
        }
        return null
    }
}