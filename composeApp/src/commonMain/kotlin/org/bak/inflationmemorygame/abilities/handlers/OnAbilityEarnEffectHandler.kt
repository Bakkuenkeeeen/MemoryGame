package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.abilities.StatusEffect
import org.bak.inflationmemorygame.game.PlayerState

interface OnAbilityEarnEffectHandler : EffectHandler {

    fun dispatch(param: Param): Result

    class Param(val earnedPlayer: PlayerState)
    class Result(val gainedEffect: StatusEffect? = null)
}