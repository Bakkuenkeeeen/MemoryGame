package org.bak.inflationmemorygame.game

import androidx.compose.ui.text.AnnotatedString
import kotlin.random.Random

data class LogMessage(val message: AnnotatedString) {
    val instanceId = Random.nextLong()
}
