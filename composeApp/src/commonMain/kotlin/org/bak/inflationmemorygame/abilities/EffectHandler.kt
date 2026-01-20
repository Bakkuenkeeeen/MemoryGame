package org.bak.inflationmemorygame.abilities

interface EffectHandler {
    val priority: Int

    companion object {
        const val PRIORITY_DEFAULT = 0
    }
}