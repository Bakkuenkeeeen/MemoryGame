package org.bak.inflationmemorygame.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

@Composable
fun isRealWindowWidthCompact(): Boolean {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val isCompact by remember {
        derivedStateOf {
            val widthDp = with(density) { windowInfo.containerSize.width.toDp() }
            widthDp < 600.dp
        }
    }
    return isCompact
}