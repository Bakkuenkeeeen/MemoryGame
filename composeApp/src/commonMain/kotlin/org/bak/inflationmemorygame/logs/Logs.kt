package org.bak.inflationmemorygame.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.message_discover_suffix
import inflationmemorygame.composeapp.generated.resources.message_effect_activate_suffix
import inflationmemorygame.composeapp.generated.resources.message_effect_deactivate_suffix
import inflationmemorygame.composeapp.generated.resources.message_effect_mistake_suffix
import inflationmemorygame.composeapp.generated.resources.message_match_suffix
import inflationmemorygame.composeapp.generated.resources.message_progress_not_flippable
import inflationmemorygame.composeapp.generated.resources.message_stage_progress_prefix
import inflationmemorygame.composeapp.generated.resources.message_tag_discover
import inflationmemorygame.composeapp.generated.resources.message_tag_effect
import inflationmemorygame.composeapp.generated.resources.message_tag_match
import inflationmemorygame.composeapp.generated.resources.message_tag_progress
import inflationmemorygame.composeapp.generated.resources.message_turn_progress_prefix
import kotlinx.coroutines.Job
import org.bak.inflationmemorygame.values.Colors
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

private val FixedMessageFrontSize = 12.sp
private val FixedMessageLineHeight = 14.sp
private val ParameterMessageFrontSize = 14.sp
private val ParameterMessageLineHeight = 16.sp

@Stable
sealed class Logs(
    val tag: StringResource,
    val color: Color,
    private val messageBuilder: @Composable () -> AnnotatedString
) {
    constructor(
        tag: StringResource,
        color: Color,
        fixedMessageRes: StringResource
    ) : this(tag, color, { buildMessage(fixedMessageRes) })

    constructor(
        tag: StringResource,
        color: Color,
        prefixRes: StringResource? = null,
        parameter: String,
        suffixRes: StringResource? = null
    ) : this(tag, color, { buildMessage(prefixRes, parameter, suffixRes) })

    val instanceId = Random.nextLong()

    val message: AnnotatedString @Composable get() = messageBuilder()

    var visibility by mutableStateOf(Visibility.Appearing)
        private set

    private val showJob = Job()
    internal suspend fun awaitAppear() = showJob.join()

    internal fun onShow() {
        visibility = Visibility.Appeared
        showJob.complete()
    }

    internal fun requestDismiss() {
        visibility = Visibility.Disappearing
    }

    internal fun onDismiss() {
        visibility = Visibility.Disappeared
    }

    enum class Visibility {
        Appearing,
        Appeared,
        Disappearing,
        Disappeared
    }

    class StartState(stage: Int) : Logs(
        tag = Res.string.message_tag_progress,
        color = Colors.ProgressMessageColor,
        prefixRes = Res.string.message_stage_progress_prefix,
        parameter = stage.toString()
    )

    class StartTurn(turn: Int) : Logs(
        tag = Res.string.message_tag_progress,
        color = Colors.ProgressMessageColor,
        prefixRes = Res.string.message_turn_progress_prefix,
        parameter = turn.toString()
    )

    class NotFlippable : Logs(
        tag = Res.string.message_tag_progress,
        color = Colors.ProgressMessageColor,
        fixedMessageRes = Res.string.message_progress_not_flippable
    )

    class Discover(name: String) : Logs(
        tag = Res.string.message_tag_discover,
        color = Colors.DiscoverMessageColor,
        parameter = name,
        suffixRes = Res.string.message_discover_suffix
    )

    class Match(name: String) : Logs(
        tag = Res.string.message_tag_match,
        color = Colors.MatchMessageColor,
        parameter = name,
        suffixRes = Res.string.message_match_suffix
    )

    class EffectActivate(name: String) : Logs(
        tag = Res.string.message_tag_effect,
        color = Colors.AbilityMessageColor,
        parameter = name,
        suffixRes = Res.string.message_effect_activate_suffix
    )

    class EffectDeactivate(name: String) : Logs(
        tag = Res.string.message_tag_effect,
        color = Colors.AbilityMessageColor,
        parameter = name,
        suffixRes = Res.string.message_effect_deactivate_suffix
    )

    class EffectMistake(name: String) : Logs(
        tag = Res.string.message_tag_effect,
        color = Colors.AbilityMessageColor,
        parameter = name,
        suffixRes = Res.string.message_effect_mistake_suffix
    )
}

@Composable
private fun buildMessage(fixedMessageRes: StringResource) = buildAnnotatedString {
    val message = stringResource(fixedMessageRes)
    withStyle(style = ParagraphStyle(lineHeight = FixedMessageLineHeight)) {
        verbose(text = message)
    }
}

@Composable
private fun buildMessage(
    prefixRes: StringResource?,
    parameter: String,
    suffixRes: StringResource?
) = buildAnnotatedString {
    val prefix = prefixRes?.let { stringResource(it) }
    val suffix = suffixRes?.let { stringResource(it) }
    withStyle(style = ParagraphStyle(lineHeight = ParameterMessageLineHeight)) {
        prefix?.let { verbose(text = it) }
        important(text = parameter)
        suffix?.let { verbose(text = it) }
    }
}

private fun AnnotatedString.Builder.verbose(text: String) {
    withStyle(style = SpanStyle(fontSize = FixedMessageFrontSize)) {
        append(text)
    }
}

private fun AnnotatedString.Builder.important(text: String) {
    withStyle(
        style = SpanStyle(fontSize = ParameterMessageFrontSize, fontWeight = FontWeight.Bold)
    ) {
        append(text)
    }
}