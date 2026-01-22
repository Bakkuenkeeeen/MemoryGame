package org.bak.inflationmemorygame.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import org.bak.inflationmemorygame.fontFamily
import org.bak.inflationmemorygame.game.LogMessage
import org.bak.inflationmemorygame.game.LogMessageState

@Composable
fun LogArea(modifier: Modifier = Modifier.Companion, logMessageState: LogMessageState) {
    LazyColumn(
        modifier = modifier.fillMaxHeight().background(color = Color.LightGray),
        reverseLayout = true
    ) {
        items(items = logMessageState.messages, key = { it.instanceId }) {
            LogMessageRow(modifier = Modifier.animateItem(), log = it)
        }
    }
}

@Composable
fun LogMessageRow(modifier: Modifier = Modifier.Companion, log: LogMessage) {
    var targetOffset by remember { mutableFloatStateOf(100f) }
    val offset by animateFloatAsState(targetValue = targetOffset)
    LaunchedEffect(Unit) { targetOffset = 0f }
    Box(modifier = modifier) {
        HorizontalDivider(modifier = Modifier.align(Alignment.TopStart))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { translationX = offset }
        ) {
            Text(text = log.message, fontFamily = fontFamily())
        }
    }
}