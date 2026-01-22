package org.bak.inflationmemorygame.game

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

data class LogMessage(val message: AnnotatedString) {
    val instanceId = Random.nextLong()

    constructor(abilityName: String, suffix: String) : this(message = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(abilityName) }
        append(suffix)
    })
}
