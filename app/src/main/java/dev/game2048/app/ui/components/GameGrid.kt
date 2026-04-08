package dev.game2048.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.game2048.app.domain.model.Direction
import dev.game2048.app.domain.model.Tile
import dev.game2048.app.ui.modifiers.onSensorTilt
import dev.game2048.app.ui.modifiers.onSwipe
import dev.game2048.app.ui.theme.Game2048Theme
import dev.game2048.app.ui.theme.LocalGameColors

@Composable
fun GameGrid(
    board: List<List<Tile?>>,
    modifier: Modifier = Modifier,
    onMove: (Direction) -> Unit,
    animated: Boolean,
    isAccelerometerEnabled: Boolean,
    isImageEnabled: Boolean
) {
    val gameColors = LocalGameColors.current

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(gameColors.gridBackground)
            .onSwipe(enabled = !isAccelerometerEnabled) { direction -> onMove(direction) }
            .onSensorTilt(enabled = isAccelerometerEnabled) { direction -> onMove(direction) }
            .padding(8.dp)
    ) {
        val spacing = 8.dp
        val gridSize = board.size
        val cellSize = (maxWidth - spacing * (gridSize - 1)) / gridSize

        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
            repeat(gridSize) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                    repeat(gridSize) {
                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .clip(MaterialTheme.shapes.small)
                                .background(gameColors.tileEmpty)
                        )
                    }
                }
            }
        }

        val activeTiles = board.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, tile ->
                tile?.let { Triple(it, colIndex, rowIndex) }
            }
        }

        activeTiles.forEach { (tile, colIndex, rowIndex) ->
            val targetX = (cellSize + spacing) * colIndex
            val targetY = (cellSize + spacing) * rowIndex

            key(tile.id) {
                val animatedX by animateDpAsState(
                    targetValue = targetX,
                    animationSpec = tween(durationMillis = 150),
                    label = "x"
                )
                val animatedY by animateDpAsState(
                    targetValue = targetY,
                    animationSpec = tween(durationMillis = 150),
                    label = "y"
                )

                val pos: Pair<Dp, Dp> = if (animated) Pair(animatedX, animatedY) else Pair(targetX, targetY)

                TileCell(
                    value = tile.value,
                    modifier = Modifier.size(cellSize).offset(x = pos.first, y = pos.second),
                    isImageEnabled = isImageEnabled
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameGridPreview() {
    val sampleBoard = listOf(
        listOf(Tile(value = 2), Tile(value = 4), Tile(value = 8), Tile(value = 16)),
        listOf(Tile(value = 32), Tile(value = 64), Tile(value = 128), Tile(value = 256)),
        listOf(Tile(value = 512), Tile(value = 1024), Tile(value = 2048), null),
        listOf(null, Tile(value = 2), null, Tile(value = 4))
    )

    Game2048Theme {
        GameGrid(
            board = sampleBoard,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onMove = {},
            animated = false,
            isAccelerometerEnabled = false,
            isImageEnabled = false
        )
    }
}
