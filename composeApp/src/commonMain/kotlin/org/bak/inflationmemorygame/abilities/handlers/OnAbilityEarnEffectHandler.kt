package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.abilities.EffectHandlerResults
import org.bak.inflationmemorygame.game.PlayerState

class OnAbilityEarnEffectHandlerParam(val earnedPlayer: PlayerState) : EffectHandler.Param

interface OnAbilityEarnEffectHandler : EffectHandler<OnAbilityEarnEffectHandlerParam> {
    companion object {
        const val PRIORITY_PLUS_ONE = 0
    }
}