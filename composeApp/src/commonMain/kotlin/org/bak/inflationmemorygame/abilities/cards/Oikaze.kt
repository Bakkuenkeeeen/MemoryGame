package org.bak.inflationmemorygame.abilities.cards

import androidx.compose.runtime.Composable
import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.description_oikaze
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
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.logs.Logs
import org.bak.inflationmemorygame.values.Params
import org.jetbrains.compose.resources.stringResource

class OikazeCard : AbilityCard.NoFieldEffect(
    ability = Abilities.Oikaze,
    earnedAbilityFactory = ::OikazeAbility
)

class OikazeAbility : EarnedAbility(ability = Abilities.Oikaze) {

    private var count = 0

    override val description: String
        @Composable get() = stringResource(Res.string.description_oikaze, level)

    override fun onEarn(): OnAbilityEarnEffectHandler? {
        changeEffectState(to = EffectState.Ready)
        return null
    }

    override fun onLevelUp(amount: Int) = OnLevelUpEffectHandler.NoAction
    override fun onLevelDown(): OnAbilityLostEffectHandler? {
        TODO("最大まで発動した後にレベルが下がったら、その場でめくれる回数を減らす？")
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onCardFlip(): OnCardFlipEffectHandler? = null

    override fun onPairMatch(): OnPairMatchEffectHandler? {
        if (count < level) {
            count++
            return object : OnPairMatchEffectHandler {
                override val priority: Int = OnPairMatchEffectHandler.PROIORITY_OIKAZE
                override suspend fun dispatch(param: OnPairMatchEffectHandlerParam) =
                    buildEffectHandlerResults {
                        printLog(Logs.gainStatusEffect { displayName })
                        gainStatusEffect(
                            effect = StatusEffect(
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
                override val priority: Int = OnTurnEndEffectHandler.PRIORITY_OIKAZE
                override suspend fun dispatch(param: OnTurnEndEffectHandlerParam) =
                    buildEffectHandlerResults {
                        printLog(Logs.lostStatusEffect { displayName })
                        lostStatusEffect(parentInstanceId = instanceId)
                    }
            }
        } else {
            return null
        }
    }
}