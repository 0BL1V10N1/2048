package dev.game2048.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.ui.theme.Game2048Theme
import dev.game2048.app.ui.theme.GameTitle
import dev.game2048.app.ui.theme.TextLight
import dev.game2048.app.ui.theme.Tile2048
import dev.game2048.app.utils.GameConstants

@Composable
fun GameOverlay(
    state: GameState,
    winTarget: Int = GameConstants.WIN_VALUE,
    onRestart: () -> Unit,
    onContinue: () -> Unit
) {
    val isWin = state == GameState.Won
    val text = if (isWin) "YOU WIN!" else "GAME OVER"
    val nextTarget = winTarget * 2
    val buttonText = if (isWin) "Play for $nextTarget or more" else "Restart"

    val backgroundColor = if (isWin) Tile2048.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(top = 80.dp, bottom = 80.dp)
    ) {
        Text(
            text = text,
            color = TextLight,
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Button(
            onClick = if (isWin) onContinue else onRestart,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(containerColor = GameTitle),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 32.dp),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text = buttonText,
                color = TextLight,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, name = "Game Over")
@Composable
private fun GameOverlayGameOverPreview() {
    Game2048Theme {
        GameOverlay(state = GameState.Over, onRestart = {}, onContinue = {})
    }
}

@Preview(showBackground = true, name = "Win")
@Composable
private fun GameOverlayWinPreview() {
    Game2048Theme {
        GameOverlay(state = GameState.Won, onRestart = {}, onContinue = {})
    }
}
