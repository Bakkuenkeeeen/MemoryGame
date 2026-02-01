package org.bak.inflationmemorygame.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.cards.HiramekiCard
import org.bak.inflationmemorygame.abilities.cards.OikazeCard
import org.bak.inflationmemorygame.abilities.cards.PlusOneCard
import org.bak.inflationmemorygame.abilities.cards.SuperhumanCard

@Stable
class StageState(val stage: Int = 1) {

    /** 現在のターン数. */
    var turns: Int by mutableIntStateOf(0)
        private set

    /** 場札. */
    val cards = mutableStateListOf<AbilityCard>()

    fun tryStartStage(): Boolean {
        if (cards.isEmpty()) {
            Abilities.entries.forEach { ability ->
                cards.addAll(List(ability.maxOccurrenceInStage * 2) {
                    when (ability) {
                        Abilities.PlusOne -> PlusOneCard()
                        Abilities.Superhuman -> SuperhumanCard()
                        Abilities.Oikaze -> OikazeCard()
                        Abilities.Hirameki -> HiramekiCard()
                    }
                })
            }
            cards.shuffle()
            return true
        }
        return false
    }

    fun incrementTurn() {
        turns++
    }

    fun flipCard(card: AbilityCard) {
        card.changeSurface(isFaceUp = true)
    }

    fun onPairMatch(card: AbilityCard, matchedCard: AbilityCard) {
        card.onMatch()
        matchedCard.onMatch()
    }

    fun enableAllCards() {
        cards.forEach { it.isInteractionEnabled = true }
    }

    fun disableAllCards() {
        cards.forEach { it.isInteractionEnabled = false }
    }

    fun reverseAllCards(exclude: List<AbilityCard> = emptyList()) {
        cards.forEach { card ->
            if (!card.isMatched || !exclude.contains(card)) {
                card.changeSurface(isFaceUp = false)
            }
        }
    }
}

@Composable
fun rememberStageState(): StageState {
    return StageState()
}