package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.game.PlayerState

interface OnAbilityLostEffectHandler: EffectHandler {
    fun dispatch(param: Param): EffectHandler.Result

    class Param(val owner: PlayerState)
}