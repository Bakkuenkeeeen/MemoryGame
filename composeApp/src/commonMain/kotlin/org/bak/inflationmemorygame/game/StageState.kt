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
import org.bak.inflationmemorygame.abilities.cards.PlusOneCard
import kotlin.coroutines.EmptyCoroutineContext

@Stable
class StageState(val stage: Int = 1) {

    /** 現在のターン数. */
    var turns: Int by mutableIntStateOf(0)
        private set

    /** 場札. 獲得済みのカードはnullにする. */
    val cards = mutableStateListOf<AbilityCard?>()

    init {
        CoroutineScope(EmptyCoroutineContext).launch(Dispatchers.Default) {
            delay(1_000)
            repeat(10) {
                cards.add(PlusOneCard())
            }
        }
    }

    fun onTurnStart() {
        turns++
    }

    fun onFlip(card: AbilityCard) {
        card.onSurfaceChange(isFaceUp = true)
    }

    fun onPairMatch(card: AbilityCard, matchedCard: AbilityCard) {
        card.onMatch()
        matchedCard.onMatch()
    }
}

@Composable
fun rememberStageState(): StageState {
    return StageState()
}