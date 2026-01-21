package org.bak.inflationmemorygame.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.common_back
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.params.Params
import org.jetbrains.compose.resources.painterResource

private const val OFFSET_PX = -20f
private val Shape = RoundedCornerShape(size = 8.dp)
private val AnimationSpec: AnimationSpec<Float> =
    tween(durationMillis = Params.CARD_APPEAR_ANIMATION_DURATION_MILLIS)

@Composable
fun CardArea(
    modifier: Modifier = Modifier,
    card: AbilityCard,
    animate: Boolean = true,
    onClick: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (animate) {
            var targetOffset by remember { mutableFloatStateOf(OFFSET_PX) }
            val offset by animateFloatAsState(
                targetValue = targetOffset,
                animationSpec = AnimationSpec
            )
            LaunchedEffect(card.isMatched) {
                targetOffset = if (card.isMatched) OFFSET_PX else 0f
            }
            Card(
                modifier = Modifier.graphicsLayer {
                    translationY = offset
                    alpha = 1f - (offset / OFFSET_PX).coerceIn(0f, 1f)
                },
                card = card,
                onClick = onClick
            )
        } else {
            Card(card = card, onClick = onClick)
        }
    }
}

@Composable
private fun BoxScope.Card(modifier: Modifier = Modifier, card: AbilityCard, onClick: () -> Unit) {
    Image(
        painter = painterResource(if (card.isFaceUp) card.image else Res.drawable.common_back),
        contentDescription = null,
        modifier = modifier
            .aspectRatio(ratio = 0.5f)
            .clip(shape = Shape)
            .align(Alignment.Center)
            .clickable(
                enabled = card.isInteractionEnabled && !card.isMatched,
                onClick = onClick
            )
    )
}