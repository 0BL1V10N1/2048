package dev.game2048.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import dev.game2048.app.ui.theme.LocalGameColors
import dev.game2048.app.ui.theme.ScoreText
import dev.game2048.app.ui.theme.TextLight

@Composable
fun GameHeader(score: Int, bestScore: Int, undosRemaining: Int, onRestart: () -> Unit, onUndo: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TitleBadge(modifier = Modifier.weight(1f).fillMaxHeight())

        Column(
            modifier = Modifier.weight(2.2f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScoreRow(score = score, bestScore = bestScore)
            ActionRow(undosRemaining = undosRemaining, onRestart = onRestart, onUndo = onUndo)
        }
    }
}

@Composable
private fun TitleBadge(modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraSmall, modifier = modifier) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "2048",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ScoreRow(score: Int, bestScore: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ScoreBox("SCORE", score, Modifier.weight(1f))
        ScoreBox("BEST", bestScore, Modifier.weight(1f))
    }
}

@Composable
private fun ScoreBox(label: String, value: Int, modifier: Modifier = Modifier) {
    val display = formatScore(value)

    Surface(color = ScoreText, shape = MaterialTheme.shapes.extraSmall, modifier = modifier) {
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
                text = display,
                fontSize = scoreFontSize(display.length),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ActionRow(undosRemaining: Int, onRestart: () -> Unit, onUndo: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HeaderButton("NEW", onRestart, Modifier.weight(1f))
        HeaderButton(
            text = "UNDO",
            onClick = onUndo,
            modifier = Modifier.weight(1f),
            enabled = undosRemaining > 0
        )
    }
}

@Composable
private fun HeaderButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.extraSmall,
        colors = ButtonDefaults.buttonColors(
            containerColor = HeaderButtons,
            disabledContainerColor = HeaderButtons.copy(alpha = 0.4f)
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatScore(value: Int): String = "%,d".format(value).replace(',', ' ')

private fun scoreFontSize(length: Int) = when {
    length <= 5 -> 18.sp
    length <= 7 -> 15.sp
    else -> 12.sp
}

@Preview(showBackground = true, name = "Header")
@Composable
private fun GameOverlayGameHeaderPreview() {
    Game2048Theme {
        GameHeader(score = 14580, bestScore = 128364, undosRemaining = 2, onRestart = {}, onUndo = {})
    }
}
