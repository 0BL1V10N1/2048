package dev.game2048.app.ui.dialogs.settings

import dev.game2048.app.domain.model.GameSettings
import dev.game2048.app.ui.theme.Theme
import dev.game2048.app.utils.GameConstants

data class SettingsUiState(
    val isMusicEnabled: Boolean = true,
    val isAnimationEnabled: Boolean = true,
    val isAccelerometerEnabled: Boolean = false,
    val currentTheme: Theme = Theme.SYSTEM,
    val isImageEnabled: Boolean = false,
    val gridSize: Int = GameConstants.GRID_SIZE
)

fun GameSettings.toUiState(): SettingsUiState = SettingsUiState(
    isMusicEnabled = isMusicEnabled,
    isAnimationEnabled = isAnimationEnabled,
    isAccelerometerEnabled = isAccelerometerEnabled,
    currentTheme = currentTheme,
    isImageEnabled = isImageEnabled,
    gridSize = gridSize
)

fun SettingsUiState.toEntity(): GameSettings = GameSettings(
    isMusicEnabled = isMusicEnabled,
    isAnimationEnabled = isAnimationEnabled,
    isAccelerometerEnabled = isAccelerometerEnabled,
    currentTheme = currentTheme,
    isImageEnabled = isImageEnabled,
    gridSize = gridSize
)
