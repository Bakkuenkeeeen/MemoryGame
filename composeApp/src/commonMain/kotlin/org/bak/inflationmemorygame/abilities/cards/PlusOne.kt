package org.bak.inflationmemorygame.abilities.cards

import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.EffectHandlerResults
import org.bak.inflationmemorygame.abilities.StatusEffect
import org.bak.inflationmemorygame.abilities.buildEffectHandlerResults
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.logs.Logs

class PlusOneCard : AbilityCard.NoFieldEffect(
    ability = Abilities.PlusOne,
    earnedAbilityFactory = ::PlusOneAbility
)

class PlusOneAbility : EarnedAbility(ability = Abilities.PlusOne) {
    override fun onEarn(): OnAbilityEarnEffectHandler = object : OnAbilityEarnEffectHandler {
        override val priority: Int = OnAbilityEarnEffectHandler.PRIORITY_PLUS_ONE
        override suspend fun dispatch(param: OnAbilityEarnEffectHandlerParam) =
            buildEffectHandlerResults {
                printLog(Logs.gainStatusEffect(name = displayName))
                gainStatusEffect(
                    effect = StatusEffect(
                        parentAbilityInstanceId = instanceId,
                        amount = 1,
                        calculationType = StatusEffect.CalculationType.Add
                    )
                )
            }
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onCardFlip(): OnCardFlipEffectHandler? = null
    override fun onPairMatch(): OnPairMatchEffectHandler? = null
    override fun onTurnEnd(): OnTurnEndEffectHandler? = null
}