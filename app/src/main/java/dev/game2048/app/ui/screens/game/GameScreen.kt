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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.ui.components.GameGrid
import dev.game2048.app.ui.components.GameHeader
import dev.game2048.app.ui.components.GameOverlay
import dev.game2048.app.ui.theme.GameTitle
import dev.game2048.app.ui.theme.LocalGameColors
import dev.game2048.app.utils.formatGameTime
import dev.game2048.app.utils.shareGameScore
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val settings by viewModel.settings.collectAsState()

    val context = LocalContext.current
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()

    var showRecap by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.state) {
        if (uiState.state == GameState.Playing) showRecap = false
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.startTimer()
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.pauseTimer()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .drawWithContent {
                    graphicsLayer.record { this@drawWithContent.drawContent() }
                    drawContent()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GameHeader(
                score = uiState.score,
                bestScore = uiState.bestScore,
                undosRemaining = uiState.undosRemaining,
                onRestart = viewModel::restart,
                onUndo = viewModel::undo
            )

            GameGrid(
                board = uiState.board,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                onMove = viewModel::move,
                animated = settings.isAnimationEnabled,
                isAccelerometerEnabled = settings.isAccelerometerEnabled,
                usesImage = settings.usesImage
            )

            Text(
                modifier = Modifier.padding(16.dp),
                text = formatGameTime(uiState.gameTime),
                fontWeight = FontWeight.Bold,
                color = LocalGameColors.current.settings
            )
        }

        if (uiState.state != GameState.Over) {
            TopBarIcons(
                onSettingsClick = onNavigateToSettings,
                onStatsClick = onNavigateToStats
            )
        }

        if (uiState.state == GameState.Over || uiState.state == GameState.Won) {
            GameOverlay(
                uistate = uiState,
                showRecap = showRecap,
                onRequestRecap = { showRecap = true },
                onShareGrid = {
                    coroutineScope.launch {
                        val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                        shareGameScore(context, bitmap, uiState.score)
                    }
                },
                onRestart = viewModel::restart,
                onContinue = viewModel::continueGame
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
private fun TopBarIcon(icon: ImageVector, description: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val iconColor = LocalGameColors.current.settings

    IconButton(
        onClick = onClick,
        modifier = modifier.size(40.dp),
        colors = IconButtonDefaults.iconButtonColors(contentColor = GameTitle)
    ) {
        Icon(imageVector = icon, contentDescription = description, modifier = Modifier.size(28.dp), tint = iconColor)
    }
}
