package org.bak.inflationmemorygame.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.common_back
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.values.Constants
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private const val OFFSET_PX = -20f
private val Shape = RoundedCornerShape(size = 8.dp)

private val RotationAnimationSpec: AnimationSpec<Float> =
    tween(durationMillis = Constants.CARD_FLIP_ANIMATION_DURATION_MILLIS)

@Composable
private fun Modifier.appearOrDisappearAnimation(isCardMatched: Boolean): Modifier {
    var targetOffset by remember { mutableFloatStateOf(OFFSET_PX) }
    LaunchedEffect(isCardMatched) {
        targetOffset = if (isCardMatched) OFFSET_PX else 0f
    }
    val animationSpec: AnimationSpec<Float> = remember(isCardMatched) {
        tween(
            durationMillis = Constants.CARD_APPEAR_ANIMATION_DURATION_MILLIS,
            delayMillis = if (isCardMatched) Constants.MATCHED_CARD_DISAPPEAR_DELAY_MILLIS else 0
        )
    }
    val offset by animateFloatAsState(
        targetValue = targetOffset,
        animationSpec = animationSpec
    )
    return graphicsLayer {
        translationY = offset
        alpha = 1f - (offset / OFFSET_PX).coerceIn(0f, 1f)
    }
}

@Composable
fun CardArea(
    modifier: Modifier = Modifier,
    card: AbilityCard,
    animate: Boolean = true,
    onClick: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (animate) {
            // クリック時にカードを回転させるアニメーション
            // 180度回転させた状態を表面にすると絵柄が反転してしまうので、180度→360度への回転が裏→表になるようにする
            var targetRotation by remember { mutableFloatStateOf(180f) }
            val rotation by animateFloatAsState(
                targetValue = targetRotation,
                animationSpec = RotationAnimationSpec
            )
            val isFaceUp by remember {
                derivedStateOf { rotation > 270 }
            }
            LaunchedEffect(card.isFaceUp) {
                targetRotation = if (card.isFaceUp) 360f else 180f
            }
            CardShapedImage(
                modifier = modifier.align(Alignment.Center)
                    .appearOrDisappearAnimation(isCardMatched = card.isMatched)
                    .graphicsLayer { rotationY = rotation },
                illustration = if (isFaceUp) card.image else Res.drawable.common_back,
                enabled = card.isInteractionEnabled && !card.isMatched,
                onClick = onClick
            )
            VisualEffectsLayer(card = card)
        } else {
            CardShapedImage(
                modifier = modifier.align(Alignment.Center),
                illustration = if (card.isFaceUp) card.image else Res.drawable.common_back,
                enabled = card.isInteractionEnabled && !card.isMatched,
                onClick = onClick
            )
        }
    }
}

@Composable
fun CardShapedImage(
    modifier: Modifier = Modifier,
    illustration: DrawableResource,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(illustration),
        contentDescription = null,
        modifier = modifier.cardShape().clickable(enabled = enabled, onClick = onClick)
    )
}

@Composable
private fun Modifier.cardShape(): Modifier = this
    .aspectRatio(ratio = 0.5f)
    .clip(shape = Shape)

@Composable
private fun VisualEffectsLayer(card: AbilityCard) {
    when (card.visualEffect) {
        is AbilityCard.VisualEffects.Flash -> {
            // 裏の間だけ
            if (!card.isFaceUp) {
                FlashVisualEffect()
            }
        }

        null -> {
            // NOP
        }
    }
}

@Composable
private fun FlashVisualEffect() {
    var targetFraction by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        targetFraction = 1f
    }
    val fraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = repeatable(
            iterations = Constants.FLASH_VISUAL_EFFECT_TOTAL_COUNT,
            animation = tween(durationMillis = Constants.FLASH_VISUAL_EFFECT_EACH_DURATION_MILLIS)
        )
    )
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize(fraction = fraction)
                .cardShape()
                .align(Alignment.Center)
                .background(color = Color.Yellow.copy(alpha = 0.3f))
        )
    }
}