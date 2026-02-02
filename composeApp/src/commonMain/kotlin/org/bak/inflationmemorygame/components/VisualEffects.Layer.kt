package org.bak.inflationmemorygame.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.bak.inflationmemorygame.util.targetValue
import org.bak.inflationmemorygame.values.Constants

@Composable
fun BoxScope.VisualEffectsLayer(effects: List<VisualEffects>) {
    effects.forEach { effect ->
        when (effect) {
            is VisualEffects.Ripple -> {
                RippleVisualEffect(effect)
            }

            else -> {
                // NOP
            }
        }
    }
}

@Composable
private fun BoxScope.RippleVisualEffect(effect: VisualEffects.Ripple) {
    val fraction by animateFloatAsState(
        targetValue = targetValue(from = 0f, to = 1f),
        animationSpec = repeatable(
            iterations = Constants.FLASH_VISUAL_EFFECT_TOTAL_COUNT,
            animation = tween(durationMillis = Constants.FLASH_VISUAL_EFFECT_EACH_DURATION_MILLIS)
        )
    ) {
        effect.onComplete()
    }
    Box(
        modifier = Modifier
            .fillMaxSize(fraction = fraction)
            .align(Alignment.Center)
            .background(color = effect.color)
    )
}