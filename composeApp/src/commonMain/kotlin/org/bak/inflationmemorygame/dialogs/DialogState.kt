package org.bak.inflationmemorygame.dialogs

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

@Stable
interface DialogState {
    val dialogs: List<Dialogs<*>>
    suspend fun <T> showDialog(dialog: Dialogs<T>): T
    fun clearDialogs()
}

fun DialogState() = object : DialogState {
    override val dialogs: SnapshotStateList<Dialogs<*>> = mutableStateListOf()

    override suspend fun <T> showDialog(dialog: Dialogs<T>): T {
        dialogs.add(dialog)
        return dialog.awaitDismiss()
    }

    override fun clearDialogs() {
        dialogs.clear()
    }
}