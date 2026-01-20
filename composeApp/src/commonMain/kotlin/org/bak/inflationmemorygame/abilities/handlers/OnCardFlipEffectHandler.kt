package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.game.GameState

/**
 * カードをめくったとき(ペア判定前)に発動する効果.
 */
interface OnCardFlipEffectHandler : EffectHandler {
    fun dispatch(param: Param): Result

    class Param()
    class Result
}