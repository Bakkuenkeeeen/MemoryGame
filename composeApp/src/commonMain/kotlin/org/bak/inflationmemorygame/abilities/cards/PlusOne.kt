package org.bak.inflationmemorygame.abilities.cards

import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.abilities.StatusEffect
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler

class PlusOneCard : AbilityCard(actual = Abilities.PlusOne) {
    override fun onEarn(): EarnedAbility = PlusOneAbility()
    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onCardFlip(): OnCardFlipEffectHandler? = null
}

class PlusOneAbility : EarnedAbility(actual = Abilities.PlusOne) {
    override fun onEarn(): OnAbilityEarnEffectHandler = object : OnAbilityEarnEffectHandler {
        override val priority: Int = EffectHandler.PRIORITY_DEFAULT
        override fun dispatch(param: OnAbilityEarnEffectHandler.Param) =
            OnAbilityEarnEffectHandler.Result(
                gainedEffect = StatusEffect(
                    parentAbilityInstanceId = instanceId,
                    amount = 1,
                    calculationType = StatusEffect.CalculationType.Add
                )
            )
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onLost(): OnAbilityLostEffectHandler = object : OnAbilityLostEffectHandler {
        override val priority: Int = EffectHandler.PRIORITY_DEFAULT
        override fun dispatch(param: OnAbilityLostEffectHandler.Param) =
            OnAbilityLostEffectHandler.Result(
                removingEffectParentInstanceId = instanceId
            )
    }
}