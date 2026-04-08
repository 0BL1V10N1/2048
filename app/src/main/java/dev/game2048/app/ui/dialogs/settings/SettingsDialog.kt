package dev.game2048.app.ui.dialogs.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.SwipeRight
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.game2048.app.domain.model.GameSettings
import dev.game2048.app.ui.theme.GameTitle
import dev.game2048.app.ui.theme.TextLight
import dev.game2048.app.ui.theme.Theme
import dev.game2048.app.ui.theme.Tile2048
import dev.game2048.app.ui.theme.getThemeData

private val GridSizeOptions = listOf(3, 4, 5, 6)

@Composable
fun SettingsDialog(viewModel: SettingsViewModel = hiltViewModel(), onDismiss: () -> Unit, onApply: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) return

    var currentSettings by remember(uiState.settings) { mutableStateOf(uiState.settings) }
    var selectedGridSize by remember(uiState.gridSize) { mutableIntStateOf(uiState.gridSize) }

    val hasChanges = selectedGridSize != uiState.gridSize || currentSettings != uiState.settings

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFAF8EF),
        tonalElevation = 6.dp
    ) {
        SettingsContent(
            settings = currentSettings,
            selectedGridSize = selectedGridSize,
            gridChanges = selectedGridSize != uiState.gridSize,
            hasChanges = hasChanges,
            onSettingsChange = { currentSettings = it },
            onGridSizeChange = { selectedGridSize = it },
            onDismiss = onDismiss,
            onApply = {
                viewModel.applySettings(selectedGridSize, currentSettings) {
                    onApply()
                }
            }
        )
    }
}

@Composable
private fun SettingsContent(
    settings: GameSettings,
    selectedGridSize: Int,
    gridChanges: Boolean,
    hasChanges: Boolean,
    onSettingsChange: (GameSettings) -> Unit,
    onGridSizeChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Settings", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GameTitle)

        SettingsSwitchRow(
            label = "Music",
            icon = if (settings.isSoundEnabled) {
                Icons.AutoMirrored.Filled.VolumeUp
            } else {
                Icons.AutoMirrored.Filled.VolumeOff
            },
            checked = settings.isSoundEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(isSoundEnabled = it)) }
        )

        SettingsSwitchRow(
            label = "Animation",
            icon = Icons.Filled.SwipeRight,
            checked = settings.isAnimationEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(isAnimationEnabled = it)) }
        )

        SettingsSwitchRow(
            label = "Motion",
            icon = Icons.Filled.ScreenRotation,
            checked = settings.isAccelerometerEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(isAccelerometerEnabled = it)) }
        )

        SettingsSwitchRow(
            label = "Images",
            icon = Icons.Filled.Image,
            checked = settings.usesImage,
            onCheckedChange = { onSettingsChange(settings.copy(usesImage = it)) }
        )

        ThemeSection(
            currentTheme = settings.currentTheme,
            onThemeChanged = { onSettingsChange(settings.copy(currentTheme = it)) }
        )

        HorizontalDivider(color = GameTitle.copy(alpha = 0.2f))

        GridSizeSection(selectedGridSize = selectedGridSize, onSelect = onGridSizeChange)

        if (gridChanges) {
            Text(
                "⚠ Changing the size will restart the game.",
                fontSize = 12.sp,
                color = Color(0xFFC73838),
                fontWeight = FontWeight.Medium
            )
        }

        DialogButtons(hasChanges = hasChanges, onDismiss = onDismiss, onApply = onApply)
    }
}

@Composable
private fun SettingsSwitchRow(label: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = label, tint = GameTitle)
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, fontSize = 16.sp, color = GameTitle)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ThemeSection(onThemeChanged: (Theme) -> Unit, currentTheme: Theme) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Theme",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = GameTitle,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Theme.entries.forEach { theme ->
                val (label, icon, color) = getThemeData(theme)

                ThemeOption(
                    isSelected = currentTheme == theme,
                    icons = icon,
                    label = label,
                    colors = color,
                    onClick = { onThemeChanged(theme) }
                )
            }
        }
    }
}

@Composable
private fun ThemeOption(
    isSelected: Boolean,
    icons: List<ImageVector>,
    label: String,
    colors: List<Color>,
    onClick: () -> Unit
) {
    val alpha = if (isSelected) 1f else 0.4f
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(MaterialTheme.shapes.small).clickable(onClick = onClick).padding(8.dp)
    ) {
        ThemeIcon(icons = icons, colors = colors, alpha = alpha)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = GameTitle.copy(alpha = alpha),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun ThemeIcon(icons: List<ImageVector>, colors: List<Color>, alpha: Float) {
    Box(
        modifier = Modifier.size(42.dp),
        contentAlignment = Alignment.Center
    ) {
        if (icons.size > 1) {
            Icon(
                imageVector = icons[1],
                contentDescription = null,
                tint = colors[1].copy(alpha = alpha * 0.6f),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
            )
            Icon(
                imageVector = icons[0],
                contentDescription = null,
                tint = colors[0].copy(alpha = alpha),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomStart)
            )
        } else {
            Icon(
                imageVector = icons[0],
                contentDescription = null,
                tint = colors[0].copy(alpha = alpha),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun GridSizeSection(selectedGridSize: Int, onSelect: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Grid size", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = GameTitle)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GridSizeOptions.forEach { size ->
                FilterChip(
                    selected = selectedGridSize == size,
                    onClick = { onSelect(size) },
                    label = { Text(text = "$size×$size", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Tile2048,
                        selectedLabelColor = TextLight
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DialogButtons(hasChanges: Boolean, onDismiss: () -> Unit, onApply: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = onDismiss) { Text("Cancel", fontWeight = FontWeight.Bold, color = GameTitle) }
        TextButton(onClick = onApply, enabled = hasChanges) {
            Text(
                "Apply",
                fontWeight = FontWeight.Bold,
                color = if (hasChanges) Tile2048 else Color.Gray
            )
        }
    }
}
