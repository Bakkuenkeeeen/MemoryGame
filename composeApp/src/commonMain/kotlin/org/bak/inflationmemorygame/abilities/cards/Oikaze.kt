package org.bak.inflationmemorygame.abilities.cards

import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.abilities.StatusEffect
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.values.Params

class OikazeCard : AbilityCard.NoFieldEffect(actual = Abilities.Oikaze) {
    override fun onEarn(): EarnedAbility {
        return OikazeAbility()
    }
}

class OikazeAbility : EarnedAbility(actual = Abilities.Oikaze) {
    override fun onEarn(): OnAbilityEarnEffectHandler? {
        changeEffectState(to = EffectState.Ready)
        return null
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onCardFlip(): OnCardFlipEffectHandler? = null

    override fun onPairMatch(): OnPairMatchEffectHandler? {
        if (tryChangeEffectState(from = EffectState.Ready, to = EffectState.Active)) {
            return object : OnPairMatchEffectHandler {
                override val priority: Int = EffectHandler.PRIORITY_DEFAULT
                override fun dispatch(param: OnPairMatchEffectHandler.Param): EffectHandler.Result {
                    return EffectHandler.Result(
                        abilityName = displayName,
                        gainedEffect = StatusEffect(
                            parentAbilityInstanceId = instanceId,
                            amount = Params.OIKAZE_AMOUNT,
                            calculationType = StatusEffect.CalculationType.Add
                        )
                    )
                }
            }
        }
        return null
    }

    override fun onTurnEnd(): OnTurnEndEffectHandler? {
        if (tryChangeEffectState(from = EffectState.Active, to = EffectState.Ready)) {
            // このターン内に発動した効果をリセット
            return object : OnTurnEndEffectHandler {
                override val priority: Int = EffectHandler.PRIORITY_DEFAULT
                override fun dispatch(param: OnTurnEndEffectHandler.Param): EffectHandler.Result {
                    return EffectHandler.Result(
                        abilityName = displayName,
                        lostEffectParentInstanceId = instanceId
                    )
                }
            }
        } else {
            return null
        }
    }
}