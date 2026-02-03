package org.bak.inflationmemorygame.abilities.cards

import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.components.VisualEffects
import org.bak.inflationmemorygame.game.LogMessage
import org.bak.inflationmemorygame.values.LogMessages

class HiramekiCard : AbilityCard.NoFieldEffect(actual = Abilities.Hirameki) {
    override fun onEarn(): EarnedAbility = HiramekiAbility()
}

class HiramekiAbility : EarnedAbility(actual = Abilities.Hirameki) {
    override fun onEarn(): OnAbilityEarnEffectHandler? {
        changeEffectState(to = EffectState.Ready)
        return null
    }

    override fun onCardFlip(): OnCardFlipEffectHandler? {
        if (tryChangeEffectState(from = EffectState.Ready, to = EffectState.Active)) {
            return object : OnCardFlipEffectHandler {
                override val priority: Int = EffectHandler.PRIORITY_DEFAULT
                override suspend fun dispatch(param: OnCardFlipEffectHandler.Param): EffectHandler.Result {
                    // 未獲得かつめくられていないカードを探す
                    val match = param.gameStateViewModel.currentStage.cards.filter {
                        !it.isMatched && !it.isFaceUp &&
                                it.displayName == param.flippedCard.displayName &&
                                it.instanceId != param.flippedCard.instanceId
                    }.randomOrNull()
                    val message = if (match == null) {
                        // 対象がなければ再使用可能にしておく
                        changeEffectState(to = EffectState.Ready)
                        LogMessage(displayName, LogMessages.EFFECT_NOT_ACTIVATED)
                    } else {
                        changeEffectState(to = EffectState.End)
                        param.gameStateViewModel.applyOneTimeVisualEffects(
                            card = match,
                            effect = VisualEffects.Ripple(),
                            awaitCompletion = false
                        )
                        LogMessage(displayName, LogMessages.EFFECT_ACTIVATED)
                    }
                    return EffectHandler.Result(message = message)
                }
            }
        }
        return null
    }

    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onPairMatch(): OnPairMatchEffectHandler? = null
    override fun onTurnEnd(): OnTurnEndEffectHandler? = null
}