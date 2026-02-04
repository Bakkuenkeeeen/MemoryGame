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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.confirm_end_turn_message
import inflationmemorygame.composeapp.generated.resources.confirm_end_turn_title
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.util.targetValue

sealed class Dialogs {
    abstract val result: Job
    open fun onDismissRequest() {
    }

    @Immutable
    class CardDetail(
        val card: AbilityCard,
        private val onDismissRequest: (CardDetail) -> Unit
    ) : Dialogs() {
        private val mResult: CompletableJob = Job()
        override val result: Job get() = mResult
        override fun onDismissRequest() {
            onDismissRequest.invoke(this)
            mResult.complete()
        }
    }

    @Immutable
    class ConfirmEndTurn(
        private val onDismissRequest: (ConfirmEndTurn) -> Unit
    ) : Dialogs() {
        private val mResult = CompletableDeferred<Boolean>()
        override val result: Deferred<Boolean> get() = mResult
        override fun onDismissRequest() {
            onDismissRequest.invoke(this)
            mResult.complete(value = false)
        }

        fun onConfirm() {
            onDismissRequest.invoke(this)
            mResult.complete(value = true)
        }
    }
}

@Composable
fun Dialogs(dialogs: List<Dialogs>) {
    dialogs.forEach { dialog ->
        AnimatedVisibility(
            visible = targetValue(from = false, to = true),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            DialogLayer(dialog = dialog)
        }
    }
}

@Composable
private fun DialogLayer(dialog: Dialogs) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray.copy(alpha = 0.75f))
            .safeDrawingPadding()
            .clickable(interactionSource = interactionSource, indication = null) {
                dialog.onDismissRequest()
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
private fun Dialog(modifier: Modifier = Modifier.Companion, dialog: Dialogs) {
    when (dialog) {
        is Dialogs.CardDetail -> CardDetailDialog(modifier = modifier, ability = dialog.card)
        is Dialogs.ConfirmEndTurn -> ConfirmDialog(
            modifier = modifier,
            title = Res.string.confirm_end_turn_title,
            message = Res.string.confirm_end_turn_message,
            onConfirm = { dialog.onConfirm() },
            onCancel = { dialog.onDismissRequest() }
        )
    }
}
