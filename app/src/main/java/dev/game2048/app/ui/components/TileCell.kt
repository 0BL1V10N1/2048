package dev.game2048.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.unit.sp
import dev.game2048.app.ui.theme.LocalGameColors
import dev.game2048.app.ui.theme.TileImages
import kotlin.math.sqrt

@Composable
fun TileCell(value: Int, modifier: Modifier = Modifier, isImageEnabled: Boolean = false) {
    val gameColors = LocalGameColors.current

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .background(gameColors.tileColor(value))
            .then(
                if (isImageEnabled && value > 0) {
                    Modifier.paint(
                        painterResource(id = TileImages.forValue(value)),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (value > 0 && !isImageEnabled) {
            val display = formatTileValue(value)
            Text(
                text = display,
                fontSize = (maxWidth.value * 0.45f / sqrt(display.length.toFloat())).sp,
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
