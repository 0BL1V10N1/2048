package dev.game2048.app.domain.model

import dev.game2048.app.ui.theme.Theme
import dev.game2048.app.utils.GameConstants

data class GameSettings(
    val isMusicEnabled: Boolean = true,
    val isAnimationEnabled: Boolean = true,
    val isAccelerometerEnabled: Boolean = false,
    val currentTheme: Theme = Theme.SYSTEM,
    val isImageEnabled: Boolean = false,
    val gridSize: Int = GameConstants.GRID_SIZE
)
