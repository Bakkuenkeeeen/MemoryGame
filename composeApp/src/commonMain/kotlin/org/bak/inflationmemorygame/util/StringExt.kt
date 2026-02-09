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
fun String.applyInnerAttributes(
    normalFontSize: Int,
    bigFontSizeAmount: Int,
    lineSpacing: Int = Constants.LINE_SPACING_DEFAULT
) = buildAnnotatedString {
    val substrings = split(stringResource(Res.string.separator_big_bold))
    if (substrings.size > 1) {
        pushStyle(ParagraphStyle(lineHeight = (normalFontSize + bigFontSizeAmount + lineSpacing).sp))
    } else {
        pushStyle(ParagraphStyle(lineHeight = (normalFontSize + lineSpacing).sp))
    }
    substrings.forEachIndexed { index, substring ->
        if (index % 2 == 0) {
            withStyle(SpanStyle(fontSize = normalFontSize.sp)) {
                append(substring)
            }
        } else {
            withStyle(
                SpanStyle(
                    fontSize = (normalFontSize + bigFontSizeAmount).sp,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(substring)
            }
        }
    }
}