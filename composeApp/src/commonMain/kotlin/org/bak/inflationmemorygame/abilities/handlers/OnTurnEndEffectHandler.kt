package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.game.PlayerState
import org.bak.inflationmemorygame.game.StageState

class OnTurnEndEffectHandlerParam(
    val stageState: StageState,
    val player: PlayerState
) : EffectHandler.Param

/**
 * ターン終了時に発動する効果.
 */
interface OnTurnEndEffectHandler : EffectHandler<OnTurnEndEffectHandlerParam> {
    companion object {
        const val PRIORITY_HOLD = 0

        // ターン終了時に効果が切れる系の能力は最後に発動するようにしておく
        const val PRIORITY_SUPERHUMAN = 999
        const val PRIORITY_OIKAZE = 999
    }
}