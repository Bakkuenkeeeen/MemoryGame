package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.EffectHandler

class OnAnotherAbilityEarnEffectHandlerParam() : EffectHandler.Param

/**
 * 新たな能力獲得時に発動する効果.
 */
interface OnAnotherAbilityEarnEffectHandler : EffectHandler<OnAnotherAbilityEarnEffectHandlerParam> {

}