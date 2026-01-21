package org.bak.inflationmemorygame.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.game.StageState

@Composable
fun BoardArea(
    modifier: Modifier = Modifier.Companion,
    stageState: StageState,
    cardsInEachRow: Int,
    rowsInBoard: Int,
    onCardClick: (AbilityCard) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(rowsInBoard) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(cardsInEachRow) { index ->
                    val card = remember(rowIndex, index) {
                        stageState.cards[rowIndex * cardsInEachRow + index]
                    }
                    CardArea(modifier = Modifier.weight(1f).fillMaxHeight(), card = card) {
                        onCardClick(card)
                    }
                }
            }
        }
    }
}