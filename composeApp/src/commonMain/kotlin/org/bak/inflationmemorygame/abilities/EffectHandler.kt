package org.bak.inflationmemorygame.abilities

import androidx.compose.ui.unit.Constraints
import org.bak.inflationmemorygame.game.LogMessage
import org.bak.inflationmemorygame.values.LogMessages

interface EffectHandler {
    val priority: Int

    companion object {
        const val PRIORITY_DEFAULT = 0
    }

    open class Result(
        val message: LogMessage? = null,
        val gainedEffects: List<StatusEffect> = emptyList(),
        val lostEffectParentInstanceIds: List<Long> = emptyList(),
        val additionalFlippedCards: List<AbilityCard> = emptyList()
    ) {
        constructor(abilityName: String, gainedEffect: StatusEffect) : this(
            message = LogMessage(abilityName, LogMessages.EFFECT_ACTIVATED),
            gainedEffects = listOf(gainedEffect)
        )

        constructor(abilityName: String, lostEffectParentInstanceId: Long) : this(
            message = LogMessage(abilityName, LogMessages.EFFECT_ACTIVATED),
            lostEffectParentInstanceIds = listOf(lostEffectParentInstanceId)
        )
    }
}