package org.bak.inflationmemorygame.abilities

import org.bak.inflationmemorygame.logs.Logs

interface EffectHandler {
    val priority: Int

    companion object {
        const val PRIORITY_DEFAULT = 0
    }

    open class Result(
        val log: Logs? = null,
        val gainedEffects: List<StatusEffect> = emptyList(),
        val lostEffectParentInstanceIds: List<Long> = emptyList(),
        val additionalFlippedCards: List<AbilityCard> = emptyList()
    ) {
        /**
         * 能力発動時用.
         */
        constructor(abilityName: String, gainedEffect: StatusEffect) : this(
            log = Logs.EffectActivate(abilityName),
            gainedEffects = listOf(gainedEffect)
        )

        /**
         * 能力喪失時用.
         */
        constructor(abilityName: String, lostEffectParentInstanceId: Long) : this(
            log = Logs.EffectDeactivate(abilityName),
            lostEffectParentInstanceIds = listOf(lostEffectParentInstanceId)
        )
    }
}