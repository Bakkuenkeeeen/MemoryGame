package org.bak.inflationmemorygame.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

@Stable
class GameState(
    private val stages: SnapshotStateList<StageState>,
    private val players: SnapshotStateList<PlayerState>
) {

    val isStageReady: Boolean by derivedStateOf { currentStage.cards.isNotEmpty() }

    val currentStage: StageState by derivedStateOf { stages.maxBy { it.stage } }

    val currentPlayer: PlayerState by derivedStateOf {
        players[(currentStage.turns - 1) % players.size]
    }
}

@Composable
fun rememberSinglePlayerGameState(
    stageState: StageState = rememberStageState(),
    playerState: PlayerState = rememberPlayerState()
): GameState {
    return GameState(
        stages = mutableStateListOf(stageState),
        players = mutableStateListOf(playerState)
    ).also {
        stageState.gameState = { it }
        playerState.joiningGameState = { it }
    }
}