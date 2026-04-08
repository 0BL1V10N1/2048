package dev.game2048.app.domain.model

import dev.game2048.app.ui.theme.Theme

data class GameSettings(
    val isSoundEnabled: Boolean = true,
    val isAnimationEnabled: Boolean = true,
    val isAccelerometerEnabled: Boolean = false,
    val currentTheme: Theme = Theme.SYSTEM,
    val usesImage: Boolean = false
)
