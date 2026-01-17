package org.bak.inflationmemorygame.game

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.components.CardCell
import org.bak.inflationmemorygame.components.LoadingScreen

@Composable
fun GameScreen(state: GameState = rememberSinglePlayerGameState()) {
    if (state.isStageReady) {
        BoardArea(stageState = state.currentStage) {
            it.isFaceUp = !it.isFaceUp
        }
    } else {
        LoadingScreen()
    }
}

@Composable
fun BoardArea(
    stageState: StageState,
    onCardClick: (AbilityCard) -> Unit
) {
    LazyVerticalGrid(columns = GridCells.Fixed(10), modifier = Modifier.fillMaxSize()) {
        items(stageState.cards) {
            CardCell(card = it) { onCardClick(it!!) }
        }
    }
}

@Composable
fun PlayerStatusArea(
    playerState: PlayerState
) {

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