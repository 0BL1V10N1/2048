package dev.game2048.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.game2048.app.ui.theme.tileColor
import dev.game2048.app.ui.theme.tileTextColor

@Composable
fun TileCell(value: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .background(tileColor(value)),
        contentAlignment = Alignment.Center
    ) {
        if (value > 0) {
            val text = formatTile(value)

            Text(
                text = text,
                fontSize = tileFontSize(text),
                fontWeight = FontWeight.Bold,
                color = tileTextColor(value)
            )
        }
    }
}

fun formatTile(value: Int): String = when {
    value >= 1000000 -> "${value / 1000000}M"
    value >= 1000 -> "${value / 1000}K"
    else -> value.toString()
}

fun tileFontSize(value: String) = when (value.length) {
    1, 2 -> 36.sp
    3 -> 30.sp
    4 -> 24.sp
    else -> 20.sp
}
