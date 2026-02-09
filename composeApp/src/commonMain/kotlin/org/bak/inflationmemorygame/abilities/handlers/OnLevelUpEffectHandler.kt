package org.bak.inflationmemorygame.abilities.handlers

import org.bak.inflationmemorygame.abilities.EffectHandler
import org.bak.inflationmemorygame.abilities.EffectHandlerResults
import org.bak.inflationmemorygame.game.PlayerState

class OnLevelUpEffectHandlerParam(val player: PlayerState) : EffectHandler.Param

interface OnLevelUpEffectHandler : EffectHandler<OnLevelUpEffectHandlerParam> {

    // 複数の能力を一気にレベルアップさせる場合は検討必要
    override val priority: Int get() = 0

    companion object {
        val NoAction = object : OnLevelUpEffectHandler {
            override suspend fun dispatch(param: OnLevelUpEffectHandlerParam): List<EffectHandlerResults> =
                emptyList()
        }
    }
}