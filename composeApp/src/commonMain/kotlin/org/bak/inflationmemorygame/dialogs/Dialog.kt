package org.bak.inflationmemorygame.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.confirm_end_turn_message
import inflationmemorygame.composeapp.generated.resources.confirm_end_turn_title
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import org.bak.inflationmemorygame.abilities.Ability
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.util.targetValue

@Stable
sealed class Dialogs<TResult> {

    var isActive: Boolean by mutableStateOf(true)
        protected set

    fun dismiss(result: TResult? = null) {
        isActive = false
        setResult(result = result)
    }

    protected abstract fun setResult(result: TResult?)
    abstract suspend fun awaitDismiss(): TResult

    sealed class NoResult : Dialogs<Unit>() {
        private val result: CompletableJob = Job()

        override fun setResult(result: Unit?) {
            this.result.complete()
        }

        override suspend fun awaitDismiss() {
            result.join()
        }
    }

    sealed class WithResult<TResult> : Dialogs<TResult>() {
        protected val result = CompletableDeferred<TResult>()
        protected abstract val defaultValue: TResult

        override fun setResult(result: TResult?) {
            this.result.complete(value = result ?: defaultValue)
        }

        override suspend fun awaitDismiss(): TResult {
            return result.await()
        }
    }

    class CardDetail(card: AbilityCard) : NoResult(), Ability by card
    class ConfirmEndTurn : WithResult<Boolean>() {
        override val defaultValue: Boolean = false
    }
}

@Composable
fun Dialogs(dialogs: List<Dialogs<*>>) {
    dialogs.forEach { dialog ->
        AnimatedVisibility(
            visible = targetValue(from = false, to = true) && dialog.isActive,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            DialogLayer(dialog = dialog)
        }
    }
}

@Composable
private fun DialogLayer(dialog: Dialogs<*>) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray.copy(alpha = 0.75f))
            .safeDrawingPadding()
            .clickable(interactionSource = interactionSource, indication = null) {
                dialog.dismiss(result = null)
            }
    ) {
        Dialog(
            modifier = Modifier
                .safeDrawingPadding()
                .padding(24.dp)
                .align(Alignment.Center),
            dialog = dialog
        )
    }
}

@Composable
private fun Dialog(modifier: Modifier = Modifier.Companion, dialog: Dialogs<*>) {
    when (dialog) {
        is Dialogs.CardDetail -> CardDetailDialog(
            modifier = modifier,
            abilityImage = dialog.image,
            abilityName = dialog.displayName,
            abilityDescription = dialog.description
        )

        is Dialogs.ConfirmEndTurn -> ConfirmDialog(
            modifier = modifier,
            title = Res.string.confirm_end_turn_title,
            message = Res.string.confirm_end_turn_message,
            onConfirm = { dialog.dismiss(result = true) },
            onCancel = { dialog.dismiss(result = false) }
        )
    }
}
