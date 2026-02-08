package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.game.PlayerState

class OnAbilityLostEffectHandlerParam(val owner: PlayerState) : EffectHandler.Param

interface OnAbilityLostEffectHandler : EffectHandler<OnAbilityLostEffectHandlerParam> {
    companion object {
        const val PRIORITY_DEFAULT = 0

        const val PRIORITY_HOLD_LEVEL_DOWN = 0
    }
}