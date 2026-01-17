package org.bak.inflationmemorygame.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.cards.PlusOne
import kotlin.coroutines.EmptyCoroutineContext

@Stable
class StageState(val stage: Int = 1) {

    lateinit var gameState: () -> GameState

    /** 現在のターン数. */
    var turns: Int by mutableIntStateOf(0)
        private set

    /** 場札. 獲得済みのカードはnullにする. */
    val cards = mutableStateListOf<AbilityCard?>()

    init {
        CoroutineScope(EmptyCoroutineContext).launch(Dispatchers.Default) {
            delay(1_000)
            repeat(10) {
                cards.add(PlusOne())
            }
        }
    }

    fun onTurnStart() {
        turns++
        cards.mapNotNull { it?.onTurnStart() }.sortedBy { it.priority }.forEach {
            it.dispatch(gameState())
        }
    }
}

@Composable
fun rememberStageState(): StageState {
    return StageState()
}