package org.bak.inflationmemorygame.abilities

import org.bak.inflationmemorygame.components.VisualEffects
import org.bak.inflationmemorygame.logs.Logs

inline fun buildEffectHandlerResults(
    builder: EffectHandlerResults.Builder.() -> Unit
): List<EffectHandlerResults> {
    return EffectHandlerResults.Builder().apply { builder() }.build()
}

sealed class EffectHandlerResults {

    class Builder {
        private val results = mutableListOf<EffectHandlerResults>()
        fun build(): List<EffectHandlerResults> = results.toList()

        fun printLog(log: Logs) = apply { results.add(PrintLog(log)) }
        fun gainStatusEffect(effect: StatusEffect) = apply { results.add(GainStatusEffect(effect)) }
        fun lostStatusEffect(parentInstanceId: Long) =
            apply { results.add(LostStatusEffect(parentInstanceId)) }

        fun applyVisualEffect(
            targetInstanceId: Long,
            visualEffect: VisualEffects,
            awaitCompletion: Boolean = false
        ) = apply {
            results.add(
                ApplyVisualEffect(
                    targetInstanceId = targetInstanceId,
                    visualEffect = visualEffect,
                    awaitCompletion = awaitCompletion
                )
            )
        }

        fun keepFlipped(targetInstanceId: Long) =
            apply { results.add(KeepFlipped(targetInstanceId)) }

        fun reverseCard(targetInstanceId: Long) =
            apply { results.add(ReverseCard(targetInstanceId)) }
    }

    class PrintLog(val log: Logs) : EffectHandlerResults()
    class GainStatusEffect(val effect: StatusEffect) : EffectHandlerResults()
    class LostStatusEffect(val parentInstanceId: Long) : EffectHandlerResults()
    class ApplyVisualEffect(
        val targetInstanceId: Long,
        val visualEffect: VisualEffects,
        val awaitCompletion: Boolean = false
    ) : EffectHandlerResults()

    class KeepFlipped(val targetInstanceId: Long) : EffectHandlerResults()
    class ReverseCard(val targetInstanceId: Long) : EffectHandlerResults()
}