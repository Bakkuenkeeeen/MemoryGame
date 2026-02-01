package org.bak.inflationmemorygame.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.game.StageState
import org.bak.inflationmemorygame.values.BoardAreaBackground
import kotlin.math.roundToInt

@Composable
fun BoardArea(
    modifier: Modifier = Modifier.Companion,
    stageState: StageState,
    isPreloading: Boolean,
    cardsInEachRow: Int,
    onCardClick: (AbilityCard) -> Unit
) {
    val rowsInBoard: Int by remember {
        derivedStateOf {
            val base = stageState.cards.size / cardsInEachRow
            if (base * cardsInEachRow < stageState.cards.size) {
                base + 1
            } else {
                base
            }
        }
    }
    Column(
        modifier = modifier.fillMaxSize().background(color = BoardAreaBackground)
            .padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(rowsInBoard) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(cardsInEachRow) { index ->
                    val card = remember(rowIndex, index) {
                        stageState.cards.getOrNull(rowIndex * cardsInEachRow + index)
                    }
                    if (card == null) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        CardArea(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            card = card,
                            animate = !isPreloading
                        ) {
                            onCardClick(card)
                        }
                    }
                }
            }
        }
    }
}