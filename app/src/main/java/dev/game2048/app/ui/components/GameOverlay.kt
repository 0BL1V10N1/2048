package dev.game2048.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
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

@Composable
fun GameOverlay(state: GameState, onRestart: () -> Unit) {
    val isWin = state == GameState.Won
    val text = if (isWin) "YOU WIN!" else "GAME OVER"
    val backgroundColor = if (isWin) Tile2048.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.5f)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = TextLight,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = GameTitle)
            ) {
                Text(
                    text = "Rejouer",
                    color = TextLight,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Game Over")
@Composable
private fun GameOverlayGameOverPreview() {
    Game2048Theme {
        GameOverlay(state = GameState.Over, onRestart = {})
    }
}

@Preview(showBackground = true, name = "Win")
@Composable
private fun GameOverlayWinPreview() {
    Game2048Theme {
        GameOverlay(state = GameState.Won, onRestart = {})
    }
}
