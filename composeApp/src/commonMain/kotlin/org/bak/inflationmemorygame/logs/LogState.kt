package org.bak.inflationmemorygame.logs

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

@Stable
interface LogState {
    val logs: List<Logs>
    suspend fun append(log: Logs)
    fun requestDismiss(index: Int)
}

fun LogState() = object : LogState {
    override val logs: SnapshotStateList<Logs> = mutableStateListOf()
    override suspend fun append(log: Logs) {
        logs.add(log)
        log.awaitAppear()
    }

    override fun requestDismiss(index: Int) {
        logs[index].requestDismiss()
    }
}