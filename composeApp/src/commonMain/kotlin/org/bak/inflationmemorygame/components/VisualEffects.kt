package org.bak.inflationmemorygame.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.bak.inflationmemorygame.values.Colors

sealed class VisualEffects(private val completion: (VisualEffects) -> Unit) {
    fun onComplete() {
        completion(this)
    }

    @Stable
    class Appear(completion: (VisualEffects) -> Unit) : VisualEffects(completion)

    @Stable
    class Disappear(completion: (VisualEffects) -> Unit) : VisualEffects(completion)

    @Stable
    class Flip(
        val isInitialFaceUp: Boolean,
        private val onFaceChange: () -> Unit,
        completion: (VisualEffects) -> Unit
    ) : VisualEffects(completion) {
        fun onFaceChange() {
            onFaceChange.invoke()
        }
    }

    @Stable
    class Ripple(
        val color: Color = Colors.FlashColor,
        completion: (VisualEffects) -> Unit
    ) : VisualEffects(completion)
}

@Composable
fun VisualEffected(
    modifier: Modifier = Modifier,
    effects: List<VisualEffects>,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.visualEffects(effects)) {
        content()
        VisualEffectsLayer(effects)
    }
}