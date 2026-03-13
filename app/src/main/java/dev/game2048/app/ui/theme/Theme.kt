package dev.game2048.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TextDark,
    onPrimary = Color.White,
    surface = Tile2,
    onSurface = TextDark,
    secondary = HeaderButtons,
    onSecondary = Color.White,
    onBackground = TextDark
)

@Composable
fun Game2048Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
