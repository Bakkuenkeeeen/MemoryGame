package org.bak.inflationmemorygame.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.separator_big_bold
import org.bak.inflationmemorygame.values.Constants
import org.jetbrains.compose.resources.stringResource

fun String.toVertical() = map { it }.joinToString(separator = "\n")

@Composable
fun String.applyInnerAttributes() = buildAnnotatedString {
    val substrings = split(stringResource(Res.string.separator_big_bold))
    if (substrings.size > 1) {
        pushStyle(ParagraphStyle(lineHeight = Constants.LOG_LINE_HEIGHT_BIG.sp))
    } else {
        pushStyle(ParagraphStyle(lineHeight = Constants.LOG_LINE_HEIGHT_NORMAL.sp))
    }
    substrings.forEachIndexed { index, substring ->
        if (index % 2 == 0) {
            withStyle(SpanStyle(fontSize = Constants.LOG_FONT_SIZE_NORMAL.sp)) {
                append(substring)
            }
        } else {
            withStyle(
                SpanStyle(
                    fontSize = Constants.LOG_FONT_SIZE_BIG.sp,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(substring)
            }
        }
    }
}