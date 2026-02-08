package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.game.GameStateViewModel

class OnCardFlipEffectHandlerParam(
    val gameStateViewModel: GameStateViewModel,
    val flippedCard: AbilityCard
) : EffectHandler.Param

/**
 * カードをめくったとき(ペア判定前)に発動する効果.
 */
interface OnCardFlipEffectHandler : EffectHandler<OnCardFlipEffectHandlerParam> {
    companion object {
        const val PRIORITY_HIRAMEKI = 0
    }
}