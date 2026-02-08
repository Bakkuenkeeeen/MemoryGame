package org.bak.inflationmemorygame.abilities

import org.bak.inflationmemorygame.components.VisualEffects
import org.bak.inflationmemorygame.logs.Logs

sealed class EffectHandlerResults {

    fun withLog(log: Logs) = listOf<EffectHandlerResults>(PrintLog(log = log)) + this

    class PrintLog(val log: Logs) : EffectHandlerResults() {
        companion object {
            fun onActivated(name: String) = listOf<EffectHandlerResults>(
                PrintLog(Logs.EffectActivate(name = name))
            )

            fun onDeactivated(name: String) = listOf<EffectHandlerResults>(
                PrintLog(Logs.EffectDeactivate(name = name))
            )

            fun onMistake(name: String) = listOf<EffectHandlerResults>(
                PrintLog(Logs.EffectMistake(name = name))
            )

            fun onLevelDown(name: String) = listOf<EffectHandlerResults>(
                PrintLog(Logs.LevelDown(name = name))
            )

            fun onAbilityLost(name: String) = listOf<EffectHandlerResults>(
                PrintLog(Logs.Lost(name = name))
            )
        }
    }

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