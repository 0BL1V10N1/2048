package dev.game2048.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SettingsDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onChangeGridSize: (Int) -> Unit
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
                    onNavigateToSettings = onNavigateToSettings,
                    onEnterSizeSelection = { isSelectingSize = true },
                    onSizeSelected = { size ->
                        onChangeGridSize(size)
                        isSelectingSize = false
                        onDismiss()
                    }
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
    onNavigateToSettings: () -> Unit,
    onEnterSizeSelection: () -> Unit,
    onSizeSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (!isSelectingSize) {
            MainActions(onNavigateToSettings, onEnterSizeSelection)
        } else {
            SizeSelectionActions(onSizeSelected)
        }
    }
}

@Composable
private fun MainActions(onSettings: () -> Unit, onGridSize: () -> Unit) {
    MenuButtonItem("Settings", onClick = onSettings)
    MenuButtonItem("Change grid size", onClick = onGridSize)
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
