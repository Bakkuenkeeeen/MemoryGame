package org.bak.inflationmemorygame.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.cards.PlusOneCard

@Stable
class StageState(val stage: Int = 1) {

    /** 現在のターン数. */
    var turns: Int by mutableIntStateOf(0)
        private set

    /** 場札. */
    val cards = mutableStateListOf<AbilityCard>()

    private var isStageStarted = false

    fun tryStartStage(): Boolean {
        if (!isStageStarted) {
            isStageStarted = true
            cards.addAll(List(10) { PlusOneCard() })
            return true
        }
        return false
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