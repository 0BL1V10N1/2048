package dev.game2048.app.ui.dialogs.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.game2048.app.ui.theme.Game2048Theme
import dev.game2048.app.ui.theme.GameTitle
import dev.game2048.app.ui.theme.TextLight
import dev.game2048.app.ui.theme.Tile2048

private val GridSizeOptions = listOf(3, 4, 5, 6)

@Composable
fun SettingsDialog(viewModel: SettingsViewModel = hiltViewModel(), onDismiss: () -> Unit, onApply: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedGridSize by remember(uiState.gridSize) { mutableIntStateOf(uiState.gridSize) }
    val hasChanges = selectedGridSize != uiState.gridSize

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFAF8EF),
        tonalElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text(text = "Settings", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GameTitle)

            GridSizeSection(selectedGridSize = selectedGridSize, onSelect = { selectedGridSize = it })

            if (hasChanges) {
                Text(
                    text = "⚠ Changing the size will restart the game.",
                    fontSize = 12.sp,
                    color = Color(0xFFC73838),
                    fontWeight = FontWeight.Medium
                )
            }

            DialogButtons(hasChanges = hasChanges, onDismiss = onDismiss) {
                viewModel.applyGridSize(selectedGridSize)
                onApply()
            }
        }
    }
}

@Composable
private fun GridSizeSection(selectedGridSize: Int, onSelect: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Grid size", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = GameTitle)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GridSizeOptions.forEach { size ->
                GridSizeChip(
                    size = size,
                    selected = selectedGridSize == size,
                    onSelect = onSelect,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GridSizeChip(size: Int, selected: Boolean, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    FilterChip(
        selected = selected,
        onClick = { onSelect(size) },
        label = { Text(text = "$size×$size", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Tile2048, selectedLabelColor = TextLight),
        modifier = modifier
    )
}

@Composable
private fun DialogButtons(hasChanges: Boolean, onDismiss: () -> Unit, onApply: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = onDismiss) {
            Text(text = "Cancel", fontWeight = FontWeight.Bold, color = GameTitle)
        }
        TextButton(onClick = onApply, enabled = hasChanges) {
            Text(text = "Apply", fontWeight = FontWeight.Bold, color = if (hasChanges) Tile2048 else Color.Gray)
        }
    }
}
