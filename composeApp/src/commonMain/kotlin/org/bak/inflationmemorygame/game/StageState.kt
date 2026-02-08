package org.bak.inflationmemorygame.game

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.cards.HiramekiCard
import org.bak.inflationmemorygame.abilities.cards.HoldCard
import org.bak.inflationmemorygame.abilities.cards.OikazeCard
import org.bak.inflationmemorygame.abilities.cards.PlusOneCard
import org.bak.inflationmemorygame.abilities.cards.SuperhumanCard
import org.bak.inflationmemorygame.abilities.cards.TotteokiCard

@Stable
class StageState(val stage: Int = 1) {

    /** 現在のターン数. */
    var turns: Int by mutableIntStateOf(0)
        private set

    /** 場札. */
    val cards = mutableStateListOf<AbilityCard>().apply {
        Abilities.entries.forEach { ability ->
            addAll(List(ability.maxOccurrenceInStage * 2) {
                when (ability) {
                    Abilities.PlusOne -> PlusOneCard()
                    Abilities.Superhuman -> SuperhumanCard()
                    Abilities.Oikaze -> OikazeCard()
                    Abilities.Hirameki -> HiramekiCard()
                    Abilities.Hold -> HoldCard()
                    Abilities.Totteoki -> TotteokiCard()
                }
            })
        }
        // shuffle()
    }

    fun incrementTurn() {
        turns++
    }

    fun enableAllCards() {
        cards.forEach { it.isInteractionEnabled = true }
    }

    fun disableAllCards() {
        cards.forEach { it.isInteractionEnabled = false }
    }

    fun findMatchedCard(card: AbilityCard): AbilityCard? = cards.find {
        card.displayName == it.displayName &&
                card.instanceId != it.instanceId &&
                it.isFaceUp &&
                !it.isMatched
    }
}