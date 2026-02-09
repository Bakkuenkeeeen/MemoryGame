package org.bak.inflationmemorygame.abilities.cards

import androidx.compose.runtime.Composable
import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.description_plus_one
import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.StatusEffect
import org.bak.inflationmemorygame.abilities.buildEffectHandlerResults
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnLevelUpEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnLevelUpEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.logs.Logs
import org.jetbrains.compose.resources.stringResource

class PlusOneCard : AbilityCard.NoFieldEffect(
    ability = Abilities.PlusOne,
    earnedAbilityFactory = ::PlusOneAbility
)

class PlusOneAbility : EarnedAbility(ability = Abilities.PlusOne) {
    override val description: String
        @Composable get() = stringResource(Res.string.description_plus_one, level)

    override fun onEarn(): OnAbilityEarnEffectHandler = object : OnAbilityEarnEffectHandler {
        override val priority: Int = OnAbilityEarnEffectHandler.PRIORITY_PLUS_ONE
        override suspend fun dispatch(param: OnAbilityEarnEffectHandlerParam) =
            buildEffectHandlerResults {
                printLog(Logs.gainStatusEffect { displayName })
                gainStatusEffect(
                    effect = StatusEffect(
                        parentAbilityInstanceId = instanceId,
                        amount = 1,
                        calculationType = StatusEffect.CalculationType.Add
                    )
                )
            }
    }

    override fun onLevelUp(amount: Int): OnLevelUpEffectHandler = object : OnLevelUpEffectHandler {
        override suspend fun dispatch(param: OnLevelUpEffectHandlerParam) =
            buildEffectHandlerResults {
                printLog(Logs.levelUp { displayName })
                repeat(amount) {
                    gainStatusEffect(
                        effect = StatusEffect(
                            parentAbilityInstanceId = instanceId,
                            amount = 1,
                            calculationType = StatusEffect.CalculationType.Add
                        )
                    )
                }
            }
    }

    override fun onLevelDown() = object : OnAbilityLostEffectHandler {
        override val priority: Int = OnAbilityLostEffectHandler.PRIORITY_DEFAULT
        override suspend fun dispatch(param: OnAbilityLostEffectHandlerParam) =
            buildEffectHandlerResults {
                printLog(Logs.levelDown { displayName })
                lostStatusEffect(parentInstanceId = instanceId)
            }
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onCardFlip(): OnCardFlipEffectHandler? = null
    override fun onPairMatch(): OnPairMatchEffectHandler? = null
    override fun onTurnEnd(): OnTurnEndEffectHandler? = null
}