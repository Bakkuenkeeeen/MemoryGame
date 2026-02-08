package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.EffectHandler

class OnStageEndEffectHandlerParam: EffectHandler.Param

/**
 * ステージ終了時に発動する効果.
 */
interface OnStageEndEffectHandler : EffectHandler<OnStageEndEffectHandlerParam> {

}