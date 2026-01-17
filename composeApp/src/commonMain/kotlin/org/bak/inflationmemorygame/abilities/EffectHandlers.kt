package org.bak.inflationmemorygame.abilities

import org.bak.inflationmemorygame.game.GameState

sealed interface EffectHandler {
    val priority: Int
}

interface OnAbilityEarnEffectHandler : EffectHandler {

}

/**
 * ターン開始時に発動する効果.
 */
interface OnTurnStartEffectHandler : EffectHandler {
    fun dispatch(gameState: GameState)
}

/**
 * カードをめくったとき(ペア判定前)に発動する効果.
 */
interface OnCardFlipEffectHandler : EffectHandler {
    fun dispatch(gameState: GameState, card: AbilityCard)
}

/**
 * ペア判定後・不成立時に発動する効果.
 */
interface OnPairUnmatchEffectHandler : EffectHandler {

}

/**
 * ペア判定後・成立時(能力獲得前)に発動する効果.
 */
interface OnPairMatchEffectHandler : EffectHandler {

}

/**
 * 新たな能力獲得時に発動する効果.
 */
interface OnAnotherAbilityEarnEffectHandler : EffectHandler {

}

/**
 * ターン終了時に発動する効果.
 */
interface OnTurnEndEffectHandler : EffectHandler {

}

/**
 * ステージ終了時に発動する効果.
 */
interface OnStageEndEffectHandler : EffectHandler {

}