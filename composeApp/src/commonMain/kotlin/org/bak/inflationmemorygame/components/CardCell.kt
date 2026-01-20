package org.bak.inflationmemorygame.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.common_back
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.jetbrains.compose.resources.painterResource

private const val OFFSET_PX = -20f
private val Shape = RoundedCornerShape(size = 8.dp)

@Composable
fun CardCell(modifier: Modifier = Modifier, card: AbilityCard?, onClick: () -> Unit) {
    Box(modifier = modifier.fillMaxSize()) {
        var targetOffset by remember {
            mutableFloatStateOf(if (card == null) OFFSET_PX else 0f)
        }
        val offset by animateFloatAsState(targetValue = targetOffset)
        if (card != null) {
            LaunchedEffect(card.isMatched) {
                targetOffset = if (card.isMatched) OFFSET_PX else 0f
            }
            Image(
                painter = painterResource(
                    if (card.isFaceUp) card.image else Res.drawable.common_back
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(ratio = 0.5f, matchHeightConstraintsFirst = true)
                    .clip(shape = Shape)
                    .align(Alignment.Center)
                    .clickable(
                        enabled = card.isInteractionEnabled && !card.isMatched,
                        onClick = onClick
                    )
                    .graphicsLayer {
                        translationY = offset
                        alpha = 1f - (offset / OFFSET_PX).coerceIn(0f, 1f)
                    }
            )
        }
    }
}