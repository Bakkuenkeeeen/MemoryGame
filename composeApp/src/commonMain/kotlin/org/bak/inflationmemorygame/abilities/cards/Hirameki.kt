package org.bak.inflationmemorygame.abilities.cards

import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.EffectHandlerResults
import org.bak.inflationmemorygame.abilities.buildEffectHandlerResults
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnLevelUpEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.components.VisualEffects
import org.bak.inflationmemorygame.logs.Logs
import org.bak.inflationmemorygame.values.Params

class HiramekiCard : AbilityCard.NoFieldEffect(
    ability = Abilities.Hirameki,
    earnedAbilityFactory = ::HiramekiAbility
)

class HiramekiAbility : EarnedAbility(ability = Abilities.Hirameki) {

    private val threshold get() = Params.HIRAMEKI_DEFAULT_AMOUNT - level + 1

    override val descriptionParams: Array<Any> get() = arrayOf(threshold)
    private var flippedCount = 0

    override fun onEarn(): OnAbilityEarnEffectHandler? {
        changeEffectState(to = EffectState.Ready)
        return null
    }

    override fun onLevelUp(amount: Int) = OnLevelUpEffectHandler.NoAction

    override fun onCardFlip(): OnCardFlipEffectHandler? {
        flippedCount++
        if (flippedCount >= threshold) {
            flippedCount = 0
            return object : OnCardFlipEffectHandler {
                override val priority: Int = OnCardFlipEffectHandler.PRIORITY_HIRAMEKI
                override suspend fun dispatch(param: OnCardFlipEffectHandlerParam): List<EffectHandlerResults> {
                    // 未獲得かつめくられていないカードを探す
                    val match = param.gameStateViewModel.currentStage.cards.filter {
                        !it.isMatched && !it.isFaceUp &&
                                it.id == param.flippedCard.id &&
                                it.instanceId != param.flippedCard.instanceId
                    }.randomOrNull()
                    return buildEffectHandlerResults {
                        if (match == null) {
                            // 対象がなければ再使用可能にしておく
                            changeEffectState(to = EffectState.Ready)
                            printLog(Logs.effectMistake { displayName })
                        } else {
                            changeEffectState(to = EffectState.End)
                            printLog(Logs.gainStatusEffect { displayName })
                            applyVisualEffect(
                                targetInstanceId = match.instanceId,
                                visualEffect = VisualEffects.Ripple()
                            )
                        }
                    }
                }
            }
        }
        return null
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onPairMatch(): OnPairMatchEffectHandler? = null
    override fun onTurnEnd(): OnTurnEndEffectHandler? = null
    override fun onLevelDown(): OnAbilityLostEffectHandler? = null
}