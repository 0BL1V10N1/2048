package dev.game2048.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

enum class Theme {
    SYSTEM,
    WATER
}

private val LightColorScheme = lightColorScheme(
    primary = LightGameColors.tile2048,
    onPrimary = Color.White,
    secondary = HeaderButtons,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark
)

private val DarkColorScheme = darkColorScheme(
    onPrimary = Color.Black,
    onSecondary = Color.White,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText
)

private val WaterColorScheme = darkColorScheme(
    primary = WaterPrimary,
    onPrimary = Color.Black,
    secondary = WaterSecondary,
    onSecondary = Color.Black,
    background = WaterBackground,
    onBackground = WaterText,
    surface = WaterSurface,
    onSurface = WaterText,
    error = WaterError
)

fun getThemeData(theme: Theme) = when (theme) {
    Theme.SYSTEM -> Triple(
        "Auto",
        listOf(Icons.Default.LightMode, Icons.Default.DarkMode),
        listOf(Color(0xFFE5A000), Color(0xFF6A5ACD))
    )

    Theme.WATER -> Triple("Water", listOf(Icons.Default.WaterDrop), listOf(Color(0xFF1BA3DE)))
}

@Composable
fun Game2048Theme(themeType: Theme = Theme.SYSTEM, content: @Composable () -> Unit) {
    val isDark = isSystemInDarkTheme()

    val colorScheme = when (themeType) {
        Theme.SYSTEM -> if (isDark) DarkColorScheme else LightColorScheme
        Theme.WATER -> WaterColorScheme
    }

    val gameColors = when (themeType) {
        Theme.SYSTEM -> if (isDark) DarkGameColors else LightGameColors
        Theme.WATER -> WaterGameColors
    }

    CompositionLocalProvider(LocalGameColors provides gameColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
