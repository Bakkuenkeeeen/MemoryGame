package org.bak.inflationmemorygame.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bak.inflationmemorygame.abilities.Ability
import org.bak.inflationmemorygame.components.cardShape
import org.bak.inflationmemorygame.isWindowWidthCompact
import org.jetbrains.compose.resources.painterResource

@Composable
fun CardDetailDialog(modifier: Modifier, ability: Ability) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (!isWindowWidthCompact()) {
            Spacer(modifier = Modifier.weight(0.5f))
        }
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Image(
                painter = painterResource(ability.image),
                contentDescription = null,
                modifier = Modifier.aspectRatio(0.5f).cardShape().align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = ability.displayName,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = ability.description,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        if (!isWindowWidthCompact()) {
            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}