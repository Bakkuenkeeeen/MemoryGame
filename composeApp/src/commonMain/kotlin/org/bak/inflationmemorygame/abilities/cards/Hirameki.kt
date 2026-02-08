package org.bak.inflationmemorygame.abilities.cards

import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.EffectHandlerResults
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.components.VisualEffects
import org.bak.inflationmemorygame.logs.Logs

class HiramekiCard : AbilityCard.NoFieldEffect(
    ability = Abilities.Hirameki,
    earnedAbilityFactory = ::HiramekiAbility
)

class HiramekiAbility : EarnedAbility(ability = Abilities.Hirameki) {
    override fun onEarn(): OnAbilityEarnEffectHandler? {
        changeEffectState(to = EffectState.Ready)
        return null
    }

    override fun onCardFlip(): OnCardFlipEffectHandler? {
        if (tryChangeEffectState(from = EffectState.Ready, to = EffectState.Active)) {
            return object : OnCardFlipEffectHandler {
                override val priority: Int = OnCardFlipEffectHandler.PRIORITY_HIRAMEKI
                override suspend fun dispatch(param: OnCardFlipEffectHandlerParam): List<EffectHandlerResults> {
                    // 未獲得かつめくられていないカードを探す
                    val match = param.gameStateViewModel.currentStage.cards.filter {
                        !it.isMatched && !it.isFaceUp &&
                                it.displayName == param.flippedCard.displayName &&
                                it.instanceId != param.flippedCard.instanceId
                    }.randomOrNull()
                    return if (match == null) {
                        // 対象がなければ再使用可能にしておく
                        changeEffectState(to = EffectState.Ready)
                        EffectHandlerResults.PrintLog.onMistake(name = displayName)
                    } else {
                        changeEffectState(to = EffectState.End)
                        EffectHandlerResults.ApplyVisualEffect(
                            targetInstanceId = match.instanceId,
                            visualEffect = VisualEffects.Ripple()
                        ).withLog(Logs.EffectActivate(name = displayName))
                    }
                }
            }
        }
        return null
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onPairMatch(): OnPairMatchEffectHandler? = null
    override fun onTurnEnd(): OnTurnEndEffectHandler? = null
}