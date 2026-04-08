package dev.game2048.app.ui.dialogs.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.game2048.app.domain.model.GameSettings
import dev.game2048.app.ui.theme.Theme
import dev.game2048.app.ui.theme.getThemeData

private val GridSizeOptions = listOf(3, 4, 5, 6)

@Composable
fun SettingsDialog(viewModel: SettingsViewModel = hiltViewModel(), onDismiss: () -> Unit, onApply: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    var currentSettings by remember(uiState.toEntity()) { mutableStateOf(uiState.toEntity()) }
    val hasChanges = currentSettings != uiState.toEntity()
    val gridChanged = currentSettings.gridSize != uiState.gridSize

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            ToggleSection(
                settings = currentSettings,
                onSettingsChange = { currentSettings = it }
            )

            ThemeSection(
                currentTheme = currentSettings.currentTheme,
                onThemeChanged = { currentSettings = currentSettings.copy(currentTheme = it) }
            )

            GridSizeSection(
                selectedGridSize = currentSettings.gridSize,
                gridChanged = gridChanged,
                onSelect = { currentSettings = currentSettings.copy(gridSize = it) }
            )

            ButtonRow(
                hasChanges = hasChanges,
                onDismiss = onDismiss,
                onApply = { viewModel.applySettings(currentSettings) { onApply() } }
            )
        }
    }
}

@Composable
private fun ToggleSection(settings: GameSettings, onSettingsChange: (GameSettings) -> Unit) {
    SectionCard {
        ToggleRow(
            label = "Music",
            icon = if (settings.isMusicEnabled) {
                Icons.AutoMirrored.Filled.VolumeUp
            } else {
                Icons.AutoMirrored.Filled.VolumeOff
            },
            checked = settings.isMusicEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(isMusicEnabled = it)) }
        )
        ToggleRow(
            label = "Animation",
            icon = Icons.Filled.Animation,
            checked = settings.isAnimationEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(isAnimationEnabled = it)) }
        )
        ToggleRow(
            label = "Motion",
            icon = Icons.Filled.ScreenRotation,
            checked = settings.isAccelerometerEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(isAccelerometerEnabled = it)) }
        )
        ToggleRow(
            label = "Images",
            icon = Icons.Filled.Image,
            checked = settings.isImageEnabled,
            onCheckedChange = { onSettingsChange(settings.copy(isImageEnabled = it)) }
        )
    }
}

@Composable
private fun ToggleRow(label: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = label, tint = primary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(label, fontSize = 15.sp, color = onSurface)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = primary)
        )
    }
}

@Composable
private fun ThemeSection(currentTheme: Theme, onThemeChanged: (Theme) -> Unit) {
    SectionCard {
        SectionTitle("Theme")
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Theme.entries.forEach { theme ->
                val (label, icons, colors) = getThemeData(theme)
                val isSelected = currentTheme == theme

                ThemeChip(
                    isSelected = isSelected,
                    icons = icons,
                    label = label,
                    colors = colors,
                    onClick = { onThemeChanged(theme) }
                )
            }
        }
    }
}

@Composable
private fun ThemeChip(
    isSelected: Boolean,
    icons: List<ImageVector>,
    label: String,
    colors: List<Color>,
    onClick: () -> Unit
) {
    val alpha = if (isSelected) 1f else 0.35f
    val onSurface = MaterialTheme.colorScheme.onSurface

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Box(modifier = Modifier.size(42.dp), contentAlignment = Alignment.Center) {
            if (icons.size > 1) {
                Icon(
                    icons[1],
                    null,
                    tint = colors[1].copy(alpha = alpha * 0.6f),
                    modifier = Modifier.size(22.dp).align(Alignment.TopEnd)
                )
                Icon(
                    icons[0],
                    null,
                    tint = colors[0].copy(alpha = alpha),
                    modifier = Modifier.size(22.dp).align(Alignment.BottomStart)
                )
            } else {
                Icon(icons[0], null, tint = colors[0].copy(alpha = alpha), modifier = Modifier.size(30.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = onSurface.copy(alpha = alpha),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun GridSizeSection(selectedGridSize: Int, gridChanged: Boolean, onSelect: (Int) -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    SectionCard {
        SectionTitle("Grid size")
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            GridSizeOptions.forEach { size ->
                FilterChip(
                    selected = selectedGridSize == size,
                    onClick = { onSelect(size) },
                    label = {
                        Text(
                            "${size}x$size",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = primary,
                        selectedLabelColor = onPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if (gridChanged) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "⚠ Changing the size will restart the game.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ButtonRow(hasChanges: Boolean, onDismiss: () -> Unit, onApply: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val onBg = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
    ) {
        OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cancel", fontWeight = FontWeight.Bold, color = onBg)
        }
        Button(
            onClick = onApply,
            enabled = hasChanges,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primary,
                contentColor = onPrimary,
                disabledContainerColor = onBg.copy(alpha = 0.12f),
                disabledContentColor = onBg.copy(alpha = 0.38f)
            )
        ) {
            Text("Apply", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
}
