package dev.game2048.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.game2048.app.ui.theme.tileColor
import dev.game2048.app.ui.theme.tileTextColor
import dev.game2048.app.ui.theme.formatTextValues
import dev.game2048.app.ui.theme.tileFontSize

@Composable
fun TileCell(value: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(tileColor(value)),
        contentAlignment = Alignment.Center
    ) {
        if (value > 0) {
            val text = formatTextValues(value)

            Text(
                text = text,
                fontSize = tileFontSize(text),
                fontWeight = FontWeight.Bold,
                color = tileTextColor(value)
            )
        }
    }
}
