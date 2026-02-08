package org.bak.inflationmemorygame.abilities

import org.bak.inflationmemorygame.abilities.EffectHandlerResults

interface EffectHandler<T : EffectHandler.Param> {
    val priority: Int
    suspend fun dispatch(param: T): List<EffectHandlerResults>

    interface Param
}