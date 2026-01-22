package org.bak.inflationmemorygame.game

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bak.inflationmemorygame.values.Params

@Stable
class LogMessageState(private val coroutineScope: CoroutineScope) {

    val messages = mutableStateListOf<LogMessage>()

    private val queue = Channel<LogMessage>(capacity = Channel.BUFFERED)

    init {
        coroutineScope.launch {
            for (message in queue) {
                messages.add(message)
                removeFirstMessageDelayed(coroutineScope)
                delay(Params.MESSAGE_ADD_INTERVAL_IN_MILLIS_NORMAL)
            }
        }
    }

    fun pushMessageAsync(message: String) {
        coroutineScope.launch { pushMessage(message) }
    }

    fun pushMessageAsync(message: AnnotatedString) {
        coroutineScope.launch { pushMessage(message) }
    }

    fun pushMessageAsync(message: LogMessage) {
        coroutineScope.launch { pushMessage(message) }
    }

    suspend fun pushMessage(message: String) {
        pushMessage(LogMessage(message = AnnotatedString(message)))
    }

    suspend fun pushMessage(message: AnnotatedString) {
        pushMessage(LogMessage(message = message))
    }

    suspend fun pushMessage(message: LogMessage) {
        queue.send(message)
    }

    private fun removeFirstMessageDelayed(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            delay(Params.MESSAGE_DURATION_MILLIS_NORMAL)
            messages.removeFirst()
        }
    }
}