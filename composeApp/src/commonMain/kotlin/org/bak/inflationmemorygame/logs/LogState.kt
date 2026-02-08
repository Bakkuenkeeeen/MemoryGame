package org.bak.inflationmemorygame.logs

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

@Stable
interface LogState {
    val logs: List<Logs>
    suspend fun appendLog(log: Logs)
}

fun LogState() = object : LogState {
    override val logs: SnapshotStateList<Logs> = mutableStateListOf()
    override suspend fun appendLog(log: Logs) {
        logs.add(log)
        log.awaitAppear()
    }
}