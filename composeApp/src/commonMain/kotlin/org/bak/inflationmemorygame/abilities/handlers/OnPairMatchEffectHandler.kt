package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.game.PlayerState

/**
 * ペア判定後・成立時(能力獲得前)に発動する効果.
 */
interface OnPairMatchEffectHandler : EffectHandler {

    fun dispatch(param: Param): EffectHandler.Result

    class Param(val player: PlayerState, val flippedCard: AbilityCard, val matchedCard: AbilityCard)
}