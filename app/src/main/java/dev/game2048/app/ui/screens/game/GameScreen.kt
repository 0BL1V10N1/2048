package dev.game2048.app.ui.screens.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.game2048.app.domain.model.GameSettings
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.ui.components.GameGrid
import dev.game2048.app.ui.components.GameHeader
import dev.game2048.app.ui.components.GameOverlay
import dev.game2048.app.ui.components.MenuButton
import dev.game2048.app.ui.components.SettingsDialog
import dev.game2048.app.ui.theme.Game2048Theme

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
    settings: GameSettings,
    onSettingsChanged: (GameSettings) -> Unit
) {
    val board by viewModel.board.collectAsState()
    val state by viewModel.state.collectAsState()
    val winTarget by viewModel.winTarget.collectAsState()
    val score by viewModel.score.collectAsState()
    val bestScore = 1 // by viewModel.bestScore.collectAsState()

    var showSettings by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        MenuButton(onClick = { showSettings = true })

        if (state == GameState.Over || state == GameState.Won) {
            GameOverlay(
                state = state,
                winTarget = winTarget,
                onRestart = viewModel::restart,
                onContinue = viewModel::continueGame
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state == GameState.Playing) {
                GameHeader(score, bestScore, onRestart = viewModel::restart, onUndo = viewModel::undo)
            }

            GameGrid(
                board = board,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                onMove = viewModel::move,
                animated = settings.isAnimationEnabled,
                isAccelerometerEnabled = settings.isAccelerometerEnabled
            )

            SettingsDialog(
                showDialog = showSettings,
                onDismiss = { showSettings = false },
                settings = settings,
                onSettingsChanged = onSettingsChanged,
                onChangeGridSize = { newSize ->
                    viewModel.restart(newSize)
                    showSettings = false
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameScreenPreview() {
    Game2048Theme {
        GameScreen(
            settings = GameSettings(),
            onSettingsChanged = {}
        )
    }
}
