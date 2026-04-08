package dev.game2048.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.ui.screens.game.GameUiState
import dev.game2048.app.ui.theme.Game2048Theme

@Composable
fun GameOverlay(
    uistate: GameUiState,
    showRecap: Boolean,
    onShareGrid: () -> Unit,
    onRequestRecap: () -> Unit,
    onRestart: () -> Unit,
    onContinue: () -> Unit
) {
    val isWin = uistate.state == GameState.Won

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(overlayBackground(isWin, showRecap))
            .padding(vertical = 80.dp)
    ) {
        if (isWin || !showRecap) {
            OverlayTitle(
                isWin = isWin,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        if (!isWin && showRecap) {
            RecapCard(
                score = uistate.score,
                bestScore = uistate.bestScore,
                moves = uistate.moves,
                topTile = uistate.currentTopTile,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        OverlayActions(
            isWin = isWin,
            showRecap = showRecap,
            winValue = uistate.winTarget * 2,
            onShareGrid = onShareGrid,
            onRequestRecap = onRequestRecap,
            onRestart = onRestart,
            onContinue = onContinue,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun overlayBackground(isWin: Boolean, showRecap: Boolean): Color {
    val alpha = if (isWin || !showRecap) 0.4f else 0.95f
    return if (isWin) {
        MaterialTheme.colorScheme.primary.copy(alpha = alpha)
    } else {
        MaterialTheme.colorScheme.background.copy(alpha = alpha)
    }
}

@Composable
private fun OverlayTitle(isWin: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = if (isWin) "YOU WIN!" else "GAME OVER",
        color = if (isWin) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
        fontSize = 48.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = modifier
    )
}

@Composable
private fun RecapCard(score: Int, bestScore: Int, moves: Int, topTile: Int, modifier: Modifier = Modifier) {
    val onSurface = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Game Results",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = onSurface.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        HorizontalDivider(color = onSurface.copy(alpha = 0.1f))

        RecapRow(label = "All time best", value = formatStat(bestScore), highlight = true)
        RecapRow(label = "Score", value = formatStat(score))
        RecapRow(label = "Best Tile", value = formatStat(topTile))
        RecapRow(label = "Moves", value = formatStat(moves))
    }
}

@Composable
private fun RecapRow(label: String, value: String, highlight: Boolean = false) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 15.sp, color = onSurface)
        Text(
            value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) primary else onSurface
        )
    }
}

@Composable
private fun OverlayActions(
    isWin: Boolean,
    showRecap: Boolean,
    winValue: Int,
    onShareGrid: () -> Unit,
    onRequestRecap: () -> Unit,
    onRestart: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        when {
            isWin -> {
                OverlayButton(
                    text = "Continue for $winValue",
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = onContinue
                )
            }

            !showRecap -> {
                OverlayButton(
                    text = "Share Board",
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    onClick = onShareGrid
                )
                OverlayButton(
                    text = "New Game",
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = onRequestRecap
                )
            }

            else -> {
                OverlayButton(
                    text = "Start Playing",
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = onRestart
                )
            }
        }
    }
}

@Composable
private fun OverlayButton(text: String, containerColor: Color, contentColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 32.dp)
    ) {
        Text(text = text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

private fun formatStat(value: Int): String = "%,d".format(value).replace(',', ' ')

@Preview(showBackground = true, name = "Win Phase")
@Composable
private fun GameOverlayWinPreview() {
    Game2048Theme {
        GameOverlay(
            uistate = GameUiState(state = GameState.Won, score = 20400080, bestScore = 20480, moves = 834),
            showRecap = false,
            onShareGrid = {},
            onRequestRecap = {},
            onRestart = {},
            onContinue = {}
        )
    }
}

@Preview(showBackground = true, name = "Game Over, Share Board Phase")
@Composable
private fun GameOverlayGameOverSharePreview() {
    Game2048Theme {
        GameOverlay(
            uistate = GameUiState(state = GameState.Over, score = 3456, bestScore = 15204, moves = 142),
            showRecap = false,
            onShareGrid = {},
            onRequestRecap = {},
            onRestart = {},
            onContinue = {}
        )
    }
}

@Preview(showBackground = true, name = "Game Over, Stats Phase")
@Composable
private fun GameOverlayGameOverStatsPreview() {
    Game2048Theme {
        GameOverlay(
            uistate = GameUiState(state = GameState.Over, score = 3456, bestScore = 1520004, moves = 142),
            showRecap = true,
            onShareGrid = {},
            onRequestRecap = {},
            onRestart = {},
            onContinue = {}
        )
    }
}
