package org.bak.inflationmemorygame.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.components.CardCell
import org.bak.inflationmemorygame.components.LoadingScreen

@Composable
fun GameScreen(state: GameState = rememberSinglePlayerGameState()) {
    if (state.isStageReady) {
        Column {
            BoardArea(modifier = Modifier.weight(1f), stageState = state.currentStage) {
                state.onCardClick(card = it)
            }
            PlayerStatusArea(playerState = state.currentPlayer)
        }
    } else {
        LoadingScreen()
    }
}

@Composable
fun BoardArea(
    modifier: Modifier = Modifier,
    stageState: StageState,
    onCardClick: (AbilityCard) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(10),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(stageState.cards) {
            CardCell(card = it) { onCardClick(it!!) }
        }
    }
}

@Composable
fun PlayerStatusArea(modifier: Modifier = Modifier, playerState: PlayerState) {
    Text("めくれる回数: ${playerState.flippedCount} / ${playerState.maxFlipCount}")
}

@Composable
fun StageInfoArea(stageState: StageState) {

}

@Composable
fun UtilityArea(
    onEndTurnClick: () -> Unit,
    onPauseClick: () -> Unit,
    onSettingClick: () -> Unit
) {

}