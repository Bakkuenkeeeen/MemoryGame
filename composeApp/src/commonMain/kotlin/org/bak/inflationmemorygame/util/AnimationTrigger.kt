package org.bak.inflationmemorygame.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun <T> animationTrigger(from: T, to: T): T {
    var value by remember { mutableStateOf(from) }
    LaunchedEffect(Unit) {
        value = to
    }
    return value
}