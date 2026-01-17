package org.bak.inflationmemorygame.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.common_back
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.jetbrains.compose.resources.painterResource

@Composable
fun CardCell(modifier: Modifier = Modifier, card: AbilityCard?, onClick: () -> Unit) {
    Box(modifier = modifier.fillMaxSize()) {
        if (card != null) {
            Image(
                painter = painterResource(
                    if (card.isFaceUp) card.image else Res.drawable.common_back
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(ratio = 0.5f, matchHeightConstraintsFirst = true)
                    .align(Alignment.Center)
                    .clickable(enabled = card.isInteractionEnabled, onClick = onClick)
            )
        }
    }
}