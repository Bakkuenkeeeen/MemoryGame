package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.EffectHandler

class OnPairUnmatchEffectHandlerParam: EffectHandler.Param

/**
 * ペア判定後・不成立時に発動する効果.
 */
interface OnPairUnmatchEffectHandler : EffectHandler<OnPairUnmatchEffectHandlerParam> {

}