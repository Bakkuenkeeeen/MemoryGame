package org.bak.inflationmemorygame.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import org.bak.inflationmemorygame.util.targetValue
import org.bak.inflationmemorygame.values.Constants

@Composable
fun Modifier.visualEffects(effects: List<VisualEffects>): Modifier {
    var modifier = this
    effects.forEach { effect ->
        when (effect) {
            is VisualEffects.Appear -> {
                modifier = modifier.appear(effect)
            }

            is VisualEffects.Disappear -> {
                modifier = modifier.disappear(effect)
            }

            is VisualEffects.Flip -> {
                modifier = modifier.flip(effect)
            }

            else -> {
                // NOP
            }
        }
    }
    return modifier
}

@Composable
private fun Modifier.appear(effect: VisualEffects.Appear): Modifier {
    val offset by animateFloatAsState(
        targetValue = targetValue(from = Constants.VISUAL_EFFECT_APPEAR_INITIAL_OFFSET_PX, to = 0f),
        animationSpec = tween(Constants.VISUAL_EFFECT_APPEAR_DURATION_MILLIS)
    ) {
        effect.onComplete()
    }
    return graphicsLayer {
        translationY = offset
        alpha = 1f - (offset / Constants.VISUAL_EFFECT_APPEAR_INITIAL_OFFSET_PX).coerceIn(0f, 1f)
    }
}

@Composable
private fun Modifier.disappear(effect: VisualEffects.Disappear): Modifier {
    val offset by animateFloatAsState(
        targetValue = targetValue(from = 0f, to = Constants.VISUAL_EFFECT_APPEAR_INITIAL_OFFSET_PX),
        animationSpec = tween(Constants.VISUAL_EFFECT_APPEAR_DURATION_MILLIS)
    ) {
        effect.onComplete()
    }
    return graphicsLayer {
        translationY = offset
        alpha = 1f - (offset / Constants.VISUAL_EFFECT_APPEAR_INITIAL_OFFSET_PX).coerceIn(0f, 1f)
    }
}

@Composable
private fun Modifier.flip(effect: VisualEffects.Flip): Modifier {
    val targetValue = if (effect.isInitialFaceUp) {
        targetValue(from = 360f, to = 180f)
    } else {
        targetValue(from = 180f, to = 360f)
    }
    val rotation by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(Constants.VISUAL_EFFECT_FLIP_DURATION_MILLIS)
    ) {
        effect.onComplete()
    }
    val isFaceChange by remember {
        derivedStateOf {
            if (effect.isInitialFaceUp) {
                rotation < 270f
            } else {
                rotation > 270f
            }
        }
    }
    LaunchedEffect(isFaceChange) {
        if (isFaceChange) {
            effect.onFaceChange()
        }
    }
    return graphicsLayer {
        rotationY = rotation
    }
}