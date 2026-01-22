package org.bak.inflationmemorygame.abilities

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import inflationmemorygame.composeapp.generated.resources.Res

interface EffectHandler {
    val priority: Int

    companion object {
        const val PRIORITY_DEFAULT = 0
        fun buildEffectStartedMessage(abilityName: String): AnnotatedString {
            return AnnotatedString("効果開始")
        }

        fun buildEffectEndMessage(abilityName: String): AnnotatedString {
            return AnnotatedString("効果終了")
        }

        private fun buildMessage(abilityName: String, suffix: String): AnnotatedString {
            return buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(abilityName) }
                append(suffix)
            }
        }
    }

    open class Result(
        val message: AnnotatedString? = null,
        val gainedEffects: List<StatusEffect> = emptyList(),
        val lostEffectParentInstanceIds: List<Long> = emptyList(),
        val additionalFlippedCards: List<AbilityCard> = emptyList()
    ) {
        constructor(
            message: AnnotatedString? = null,
            gainedEffect: StatusEffect? = null,
            lostEffectParentInstanceId: Long? = null,
            additionalFlippedCard: AbilityCard? = null
        ) : this(
            message = message,
            gainedEffects = gainedEffect?.let { listOf(it) } ?: emptyList(),
            lostEffectParentInstanceIds = lostEffectParentInstanceId?.let { listOf(it) }
                ?: emptyList(),
            additionalFlippedCards = additionalFlippedCard?.let { listOf(it) } ?: emptyList()
        )
    }
}