package dev.game2048.app.ui.screens.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.ui.components.GameGrid
import dev.game2048.app.ui.components.GameHeader
import dev.game2048.app.ui.components.GameOverlay
import dev.game2048.app.ui.theme.GameTitle

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.state == GameState.Over || uiState.state == GameState.Won) {
            GameOverlay(
                state = uiState.state,
                winTarget = uiState.winTarget,
                onRestart = viewModel::restart,
                onContinue = viewModel::continueGame
            )
        }

        TopBarIcons(
            onSettingsClick = onNavigateToSettings,
            onStatsClick = onNavigateToStats
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.state == GameState.Playing) {
                GameHeader(
                    uiState.score,
                    uiState.bestScore,
                    uiState.undosRemaining,
                    onRestart = viewModel::restart,
                    onUndo = viewModel::undo
                )
            }

            GameGrid(
                board = uiState.board,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                onMove = viewModel::move
            )
        }
    }
}

@Composable
private fun TopBarIcons(onSettingsClick: () -> Unit, onStatsClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        TopBarIcon(
            icon = Icons.Default.Settings,
            description = "Settings",
            onClick = onSettingsClick,
            modifier = Modifier.align(Alignment.TopStart)
        )
        TopBarIcon(
            icon = Icons.Default.BarChart,
            description = "Statistics",
            onClick = onStatsClick,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun TopBarIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(40.dp),
        colors = IconButtonDefaults.iconButtonColors(contentColor = GameTitle)
    ) {
        Icon(imageVector = icon, contentDescription = description, modifier = Modifier.size(28.dp))
    }
}
