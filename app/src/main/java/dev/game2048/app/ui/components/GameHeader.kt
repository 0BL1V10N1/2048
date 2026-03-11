package dev.game2048.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import dev.game2048.app.ui.theme.Game2048Theme
import dev.game2048.app.ui.theme.HeaderButtons
import dev.game2048.app.ui.theme.ScoreText
import dev.game2048.app.ui.theme.TextLight
import dev.game2048.app.ui.theme.Tile2048
import dev.game2048.app.ui.theme.formatTextValues

@Composable
fun GameHeader(score: Int, bestScore: Int, onRestart: () -> Unit, onUndo: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = Tile2048,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "2048",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(2.2f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScoreBox("SCORE", score, Modifier.weight(1f))
                ScoreBox("BEST", bestScore, Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DisplayButton("NEW", onRestart, Modifier.weight(1f))
                DisplayButton("UNDO", onUndo, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ScoreBox(label: String, value: Int, modifier: Modifier = Modifier) {
    Surface(
        color = ScoreText,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight.copy(alpha = 0.7f)
            )
            Text(
                text = formatTextValues(value),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun DisplayButton(text: String, action: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = action,
        modifier = modifier.height(42.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = HeaderButtons),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, name = "Header")
@Composable
private fun GameOverlayGameHeaderPreview() {
    Game2048Theme {
        GameHeader(score = 0, bestScore = 0, onRestart = {}, onUndo = {})
    }
}
