package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.game.PlayerState
import org.bak.inflationmemorygame.game.StageState

class OnTurnStartEffectHandlerParam(
    val stageState: StageState,
    val nextPlayer: PlayerState
) : EffectHandler.Param

/**
 * ターン開始時に発動する効果.
 */
interface OnTurnStartEffectHandler : EffectHandler<OnTurnStartEffectHandlerParam> {
    companion object {
        const val PRIORITY_SUPERHUMAN = 1
    }
}