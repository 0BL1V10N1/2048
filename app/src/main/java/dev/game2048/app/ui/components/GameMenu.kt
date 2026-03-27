package dev.game2048.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SwipeRight
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import dev.game2048.app.ui.theme.Theme

@Composable
fun SettingsDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    isSoundEnabled: Boolean,
    onSoundToggled: (Boolean) -> Unit,
    onChangeGridSize: (Int) -> Unit,
    currentTheme: Theme,
    onThemeChanged: (Theme) -> Unit,
    isAnimationEnabled: Boolean,
    onAnimationEnabled: (Boolean) -> Unit
) {
    var isSelectingSize by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            title = { DialogTitle(isSelectingSize) },
            text = {
                DialogContent(
                    isSelectingSize = isSelectingSize,
                    isSoundEnabled = isSoundEnabled,
                    onSoundToggled = onSoundToggled,
                    onEnterSizeSelection = { isSelectingSize = true },
                    onSizeSelected = { size ->
                        onChangeGridSize(size)
                        isSelectingSize = false
                        onDismiss()
                    },
                    currentTheme = currentTheme,
                    onThemeChanged = onThemeChanged,
                    isAnimationEnabled = isAnimationEnabled,
                    onAnimationEnabled = onAnimationEnabled
                )
            },
            confirmButton = {
                DialogNavigationButton(
                    isSelectingSize = isSelectingSize,
                    onBack = { isSelectingSize = false },
                    onClose = onDismiss
                )
            }
        )
    }
}

@Composable
fun BoxScope.MenuButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.TopStart)
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun DialogTitle(isSelectingSize: Boolean) {
    Text(
        text = if (isSelectingSize) "Select Grid Size" else "Menu",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
private fun DialogContent(
    isSelectingSize: Boolean,
    isSoundEnabled: Boolean,
    onSoundToggled: (Boolean) -> Unit,
    isAnimationEnabled: Boolean,
    onAnimationEnabled: (Boolean) -> Unit,
    onEnterSizeSelection: () -> Unit,
    onSizeSelected: (Int) -> Unit,
    currentTheme: Theme,
    onThemeChanged: (Theme) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (!isSelectingSize) {
            MainActions(
                isSoundEnabled = isSoundEnabled,
                onSoundToggled = onSoundToggled,
                onGridSize = onEnterSizeSelection,
                currentTheme = currentTheme,
                onThemeChanged = onThemeChanged,
                isAnimationEnabled = isAnimationEnabled,
                onAnimationEnabled = onAnimationEnabled
            )
        } else {
            SizeSelectionActions(onSizeSelected)
        }
    }
}

@Composable
private fun MainActions(
    isSoundEnabled: Boolean,
    onSoundToggled: (Boolean) -> Unit,
    isAnimationEnabled: Boolean,
    onAnimationEnabled: (Boolean) -> Unit,
    onGridSize: () -> Unit,
    currentTheme: Theme,
    onThemeChanged: (Theme) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        SoundSection(isSoundEnabled, onSoundToggled)
        AnimationSection(isAnimationEnabled, onAnimationEnabled)
        ThemeSection(onThemeChanged, currentTheme)
        MenuButtonItem("Change grid size", onClick = onGridSize)
    }
}

@Composable
private fun SizeSelectionActions(onSizeSelected: (Int) -> Unit) {
    WarningText("Changing size will restart the game.")

    val sizes = listOf(3, 4, 5, 6)
    sizes.chunked(2).forEach { rowSizes ->
        SizeRow(rowSizes, onSizeSelected)
    }
}

@Composable
private fun SizeRow(sizes: List<Int>, onSizeSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        sizes.forEach { size ->
            SizeButton(size, onClick = { onSizeSelected(size) })
        }
    }
}

@Composable
private fun MenuButtonItem(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label)
    }
}

@Composable
private fun RowScope.SizeButton(size: Int, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .padding(2.dp)
    ) {
        Text("${size}x$size")
    }
}

@Composable
private fun WarningText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    )
}

@Composable
private fun DialogNavigationButton(isSelectingSize: Boolean, onBack: () -> Unit, onClose: () -> Unit) {
    TextButton(onClick = if (isSelectingSize) onBack else onClose) {
        Text(if (isSelectingSize) "Back" else "Close")
    }
}

@Composable
private fun ThemeOption(isSelected: Boolean, icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    val alpha = if (isSelected) 1f else 0.4f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color.copy(alpha = alpha),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun SoundSection(isSoundEnabled: Boolean, onSoundToggled: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isSoundEnabled) {
                    Icons.AutoMirrored.Filled.VolumeUp
                } else {
                    Icons.AutoMirrored.Filled.VolumeOff
                },

                contentDescription = "sound icon",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Sound", style = MaterialTheme.typography.bodyLarge)
        }

        Switch(
            checked = isSoundEnabled,
            onCheckedChange = onSoundToggled
        )
    }
}

@Composable
private fun AnimationSection(isAnimationEnabled: Boolean, onAnimationEnabled: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.SwipeRight,
                contentDescription = "animation icon",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Animation", style = MaterialTheme.typography.bodyLarge)
        }

        Switch(
            checked = isAnimationEnabled,
            onCheckedChange = onAnimationEnabled
        )
    }
}

@Composable
private fun ThemeSection(onThemeChanged: (Theme) -> Unit, currentTheme: Theme) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Theme",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ThemeOption(
                isSelected = currentTheme == Theme.LIGHT,
                icon = Icons.Default.LightMode,
                label = "Light",
                color = Color(0xFFE5A000),
                onClick = { onThemeChanged(Theme.LIGHT) }
            )
            ThemeOption(
                isSelected = currentTheme == Theme.DARK,
                icon = Icons.Default.DarkMode,
                label = "Dark",
                color = Color(0xFF6A5ACD),
                onClick = { onThemeChanged(Theme.DARK) }
            )
            ThemeOption(
                isSelected = currentTheme == Theme.WATER,
                icon = Icons.Default.WaterDrop,
                label = "Water",
                color = MaterialTheme.colorScheme.primary,
                onClick = { onThemeChanged(Theme.WATER) }
            )
        }
    }
}
