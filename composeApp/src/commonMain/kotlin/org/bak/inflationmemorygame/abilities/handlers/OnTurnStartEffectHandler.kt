package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.game.PlayerState
import org.bak.inflationmemorygame.game.StageState

/**
 * ターン開始時に発動する効果.
 */
interface OnTurnStartEffectHandler : EffectHandler {

    companion object {
        const val PRIORITY_SUPERHUMAN = 1
    }

    fun dispatch(param: Param): EffectHandler.Result

    class Param(val stageState: StageState, val nextPlayer: PlayerState)
}