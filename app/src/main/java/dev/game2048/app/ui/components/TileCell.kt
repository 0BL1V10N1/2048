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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.game2048.app.ui.theme.ImageMode
import dev.game2048.app.ui.theme.LocalGameColors

@Composable
fun TileCell(value: Int, modifier: Modifier = Modifier, useImage: Boolean = true) {
    val gameColors = LocalGameColors.current
    val imagesMode = ImageMode()

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .background(gameColors.tileColor(value))
            .then(
                if (useImage && value > 0) {
                    Modifier.paint(
                        painterResource(id = imagesMode.image2048(value)),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (value > 0 && !useImage) {
            val display = formatTileValue(value)
            Text(
                text = display,
                fontSize = tileFontSize(display.length),
                fontWeight = FontWeight.Bold,
                color = gameColors.tileTextColor(value)
            )
        }
    }
}

private fun formatTileValue(value: Int): String = when {
    value >= 1_000_000 -> "${value / 1_000_000}M"
    value >= 10_000 -> "${value / 1_000}K"
    else -> value.toString()
}

private fun tileFontSize(length: Int): TextUnit = when (length) {
    1 -> 34.sp
    2 -> 30.sp
    3 -> 24.sp
    4 -> 20.sp
    else -> 16.sp
}
