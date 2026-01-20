package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.game.GameState

/**
 * ターン開始時に発動する効果.
 */
interface OnTurnStartEffectHandler : EffectHandler {
    fun dispatch(param: Param): Result

    class Param(val gameState: GameState)
    class Result
}