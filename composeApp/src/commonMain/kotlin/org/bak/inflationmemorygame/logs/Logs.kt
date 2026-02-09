package org.bak.inflationmemorygame.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.log_ability_level_down
import inflationmemorygame.composeapp.generated.resources.log_ability_level_up
import inflationmemorygame.composeapp.generated.resources.log_ability_lost
import inflationmemorygame.composeapp.generated.resources.log_discover
import inflationmemorygame.composeapp.generated.resources.log_effect_activate
import inflationmemorygame.composeapp.generated.resources.log_effect_deactivate
import inflationmemorygame.composeapp.generated.resources.log_effect_mistake
import inflationmemorygame.composeapp.generated.resources.log_match
import inflationmemorygame.composeapp.generated.resources.log_not_flippable
import inflationmemorygame.composeapp.generated.resources.log_start_stage
import inflationmemorygame.composeapp.generated.resources.log_start_turn
import inflationmemorygame.composeapp.generated.resources.log_tag_ability
import inflationmemorygame.composeapp.generated.resources.log_tag_discover
import inflationmemorygame.composeapp.generated.resources.log_tag_match
import inflationmemorygame.composeapp.generated.resources.log_tag_progress
import kotlinx.coroutines.Job
import org.bak.inflationmemorygame.util.applyInnerAttributes
import org.bak.inflationmemorygame.values.Colors
import org.bak.inflationmemorygame.values.Constants
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

@Stable
class Logs(val category: Category, private val messageBuilder: @Composable () -> String) {

    val instanceId = Random.nextLong()

    val message: AnnotatedString
        @Composable get() = messageBuilder().applyInnerAttributes(
            normalFontSize = Constants.FONT_SIZE_NORMAL_LOG,
            bigFontSizeAmount = Constants.BIG_FONT_SIZE_AMOUNT_LOG
        )

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

    enum class Category(val tag: StringResource, val color: Color) {
        Progress(tag = Res.string.log_tag_progress, color = Colors.ProgressMessageColor),
        Discover(tag = Res.string.log_tag_discover, color = Colors.DiscoverMessageColor),
        Match(tag = Res.string.log_tag_match, color = Colors.MatchMessageColor),
        Ability(tag = Res.string.log_tag_ability, color = Colors.AbilityMessageColor)
    }

    companion object {
        fun startStage(stage: Int) = Logs(category = Category.Progress) {
            stringResource(Res.string.log_start_stage, stage)
        }

        fun startTurn(turn: Int) = Logs(category = Category.Progress) {
            stringResource(Res.string.log_start_turn, turn)
        }

        fun notFlippable() = Logs(category = Category.Progress) {
            stringResource(Res.string.log_not_flippable)
        }

        fun discover(name: @Composable () -> String) = Logs(category = Category.Discover) {
            stringResource(Res.string.log_discover, name())
        }

        fun match(name: @Composable () -> String) = Logs(category = Category.Match) {
            stringResource(Res.string.log_match, name())
        }

        fun levelUp(name: @Composable () -> String) = Logs(category = Category.Match) {
            stringResource(Res.string.log_ability_level_up, name())
        }

        fun levelDown(name: @Composable () -> String) = Logs(category = Category.Ability) {
            stringResource(Res.string.log_ability_level_down, name())
        }

        fun lost(name: @Composable () -> String) = Logs(category = Category.Ability) {
            stringResource(Res.string.log_ability_lost, name())
        }

        fun gainStatusEffect(name: @Composable () -> String) = Logs(category = Category.Ability) {
            stringResource(Res.string.log_effect_activate, name())
        }

        fun lostStatusEffect(name: @Composable () -> String) = Logs(category = Category.Ability) {
            stringResource(Res.string.log_effect_deactivate, name())
        }

        fun effectMistake(name: @Composable () -> String) = Logs(category = Category.Ability) {
            stringResource(Res.string.log_effect_mistake, name())
        }
    }
}