package dev.game2048.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.game2048.app.data.models.Direction
import dev.game2048.app.ui.modifiers.onSwipe
import dev.game2048.app.ui.theme.GridBackground

@Composable
fun GameGrid(board: List<List<Int>>, modifier: Modifier = Modifier, onMove: (Direction) -> Unit) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(GridBackground)
            .onSwipe { direction -> onMove(direction) }
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (row in board) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (value in row) TileCell(value = value, modifier = Modifier.weight(1f))
            }
        }
    }
}
