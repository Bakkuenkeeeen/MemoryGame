package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.game.GameStateViewModel

/**
 * カードをめくったとき(ペア判定前)に発動する効果.
 */
interface OnCardFlipEffectHandler : EffectHandler {
    suspend fun dispatch(param: Param): EffectHandler.Result

    class Param(val gameStateViewModel: GameStateViewModel, val flippedCard: AbilityCard)
}