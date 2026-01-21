package org.bak.inflationmemorygame.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.bak.inflationmemorygame.components.BoardArea
import org.bak.inflationmemorygame.components.LogArea
import org.bak.inflationmemorygame.isWindowWidthCompact
import org.bak.inflationmemorygame.params.Params

@Composable
fun GameScreen(state: GameStateViewModel = gameStateViewModel()) {
    if (isWindowWidthCompact()) {
        CompactGameScreen(state = state)
    } else {
        WideGameScreen(state = state)
    }
}

@Composable
fun CompactGameScreen(state: GameStateViewModel) {
    BoardArea(
        modifier = Modifier.fillMaxSize(),
        stageState = state.currentStage,
        cardsInEachRow = Params.CARDS_IN_EACH_ROW_COMPACT,
        rowsInBoard = state.currentStage.cards.size / Params.CARDS_IN_EACH_ROW_COMPACT
    ) {
        state.onCardClick(card = it)
    }
}

@Composable
fun WideGameScreen(state: GameStateViewModel) {
    Row {
        Column(modifier = Modifier.weight(7f)) {
            BoardArea(
                modifier = Modifier.weight(1f),
                stageState = state.currentStage,
                cardsInEachRow = Params.CARDS_IN_EACH_ROW_WIDE,
                rowsInBoard = state.currentStage.cards.size / Params.CARDS_IN_EACH_ROW_WIDE
            ) {
                state.onCardClick(card = it)
            }
            PlayerStatusArea(playerState = state.currentPlayer)
        }
        LogArea(modifier = Modifier.weight(3f), logs = state.messages)
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
