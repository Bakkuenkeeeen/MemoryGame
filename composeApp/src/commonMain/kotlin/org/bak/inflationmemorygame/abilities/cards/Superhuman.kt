package org.bak.inflationmemorygame.abilities.cards

import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.StatusEffect
import org.bak.inflationmemorygame.abilities.buildEffectHandlerResults
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnLevelUpEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandlerParam
import org.bak.inflationmemorygame.logs.Logs

class SuperhumanCard : AbilityCard.NoFieldEffect(
    ability = Abilities.Superhuman,
    earnedAbilityFactory = ::SuperhumanAbility
)

class SuperhumanAbility : EarnedAbility(ability = Abilities.Superhuman) {
    override fun onEarn(): OnAbilityEarnEffectHandler? {
        changeEffectState(to = EffectState.Ready)
        return null
    }

    override fun onLevelUp(amount: Int): OnLevelUpEffectHandler {
        TODO("レベル上がらない想定")
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? {
        if (tryChangeEffectState(from = EffectState.Ready, to = EffectState.Active)) {
            return object : OnTurnStartEffectHandler {
                override val priority: Int = OnTurnStartEffectHandler.PRIORITY_SUPERHUMAN
                override suspend fun dispatch(param: OnTurnStartEffectHandlerParam) =
                    buildEffectHandlerResults {
                        printLog(Logs.gainStatusEffect { displayName })
                        gainStatusEffect(
                            effect = StatusEffect(
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

    override fun onCardFlip(): OnCardFlipEffectHandler? = null
    override fun onPairMatch(): OnPairMatchEffectHandler? = null

    override fun onTurnEnd(): OnTurnEndEffectHandler? {
        if (tryChangeEffectState(from = EffectState.Active, to = EffectState.End)) {
            return object : OnTurnEndEffectHandler {
                override val priority: Int = OnTurnEndEffectHandler.PRIORITY_SUPERHUMAN
                override suspend fun dispatch(param: OnTurnEndEffectHandlerParam) =
                    buildEffectHandlerResults {
                        printLog(Logs.lostStatusEffect { displayName })
                        lostStatusEffect(parentInstanceId = instanceId)
                    }
            }
        }
        return null
    }

    override fun onLevelDown(): OnAbilityLostEffectHandler? {
        TODO("レベル下がらない想定")
    }
}