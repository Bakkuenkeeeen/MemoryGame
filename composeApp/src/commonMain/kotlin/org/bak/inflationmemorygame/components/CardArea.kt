package org.bak.inflationmemorygame.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.common_back
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.jetbrains.compose.resources.painterResource

private val Shape = RoundedCornerShape(size = 8.dp)

@Composable
fun CardArea(
    modifier: Modifier = Modifier,
    card: AbilityCard,
    onClick: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        VisualEffected(
            modifier = modifier.align(Alignment.Center).cardShape(),
            effects = card.visualEffects
        ) {
            Image(
                painter = painterResource(if (card.isFaceUp) card.image else Res.drawable.common_back),
                contentDescription = null,
                modifier = modifier
                    .alpha(if (card.isMatched) 0f else 1f)
                    .clickable(
                        enabled = card.isInteractionEnabled && !card.isMatched,
                        onClick = onClick
                    )
            )
        }
    }
}

@Composable
fun Modifier.cardShape(): Modifier = this
    .aspectRatio(ratio = 0.5f)
    .clip(shape = Shape)
