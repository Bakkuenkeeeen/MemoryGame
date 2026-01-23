package org.bak.inflationmemorygame.game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun MenuButtonsArea(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onEndTurnClick: () -> Unit,
    onPauseClick: () -> Unit,
    onSettingClick: () -> Unit
) {
    Row(
        modifier = modifier.padding(all = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f)) // 右詰め
        Text(
            text = "ターン終了",
            modifier = Modifier.clickable(enabled = enabled, onClick = onEndTurnClick),
            textDecoration = TextDecoration.Underline
        )
    }
}