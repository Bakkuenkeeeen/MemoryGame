package org.bak.inflationmemorygame.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.bak.inflationmemorygame.components.BoardArea
import org.bak.inflationmemorygame.components.LogArea
import org.bak.inflationmemorygame.isWindowWidthCompact
import org.bak.inflationmemorygame.values.Params

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
    Column {
        MenuButtonsArea(
            enabled = state.isButtonsEnable,
            modifier = Modifier.fillMaxWidth(),
            onEndTurnClick = { state.onEndTurnClick() },
            onAutoClick = { state.onAutoClick() },
            onPauseClick = {},
            onSettingClick = {}
        )
        BoardArea(
            modifier = Modifier.fillMaxWidth().weight(8f),
            stageState = state.currentStage,
            cardsInEachRow = Params.CARDS_IN_EACH_ROW_COMPACT
        ) {
            state.onCardClick(card = it)
        }
        Row(modifier = Modifier.weight(2f)) {
            PlayerStatusArea(
                modifier = Modifier.weight(1f).fillMaxSize(),
                playerState = state.currentPlayer
            )
            LogArea(
                modifier = Modifier.weight(1f).fillMaxSize(),
                logMessageState = state.logMessageState
            )
        }
    }
}

@Composable
fun WideGameScreen(state: GameStateViewModel) {
    Column {
        MenuButtonsArea(
            enabled = state.isButtonsEnable,
            modifier = Modifier.fillMaxWidth(),
            onEndTurnClick = { state.onEndTurnClick() },
            onAutoClick = { state.onAutoClick() },
            onPauseClick = {},
            onSettingClick = {}
        )
        Row {
            Column(modifier = Modifier.weight(8f)) {
                BoardArea(
                    modifier = Modifier.weight(8f),
                    stageState = state.currentStage,
                    cardsInEachRow = Params.CARDS_IN_EACH_ROW_WIDE
                ) {
                    state.onCardClick(card = it)
                }
                PlayerStatusArea(modifier = Modifier.weight(2f), playerState = state.currentPlayer)
            }
            LogArea(modifier = Modifier.weight(2f), logMessageState = state.logMessageState)
        }
    }
}

@Composable
fun PlayerStatusArea(modifier: Modifier = Modifier, playerState: PlayerState) {
    val fontColor by remember {
        derivedStateOf {
            if (playerState.isFlippable) Color.Unspecified else Color.Red
        }
    }
    Text(
        "めくれる回数: ${playerState.flippedCount} / ${playerState.maxFlipCount}",
        modifier = modifier,
        color = fontColor
    )
}

@Composable
fun StageInfoArea(stageState: StageState) {

}

