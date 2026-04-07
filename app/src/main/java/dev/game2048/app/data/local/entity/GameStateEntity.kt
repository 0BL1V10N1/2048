package dev.game2048.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.domain.model.HistoryState
import dev.game2048.app.domain.model.Tile
import dev.game2048.app.utils.GameConstants

@Entity(tableName = "game_state")
data class GameStateEntity(
    @PrimaryKey val id: Int = 1,
    val board: List<List<Tile?>> = List(GameConstants.GRID_SIZE) { List(GameConstants.GRID_SIZE) { null } },
    val score: Int = 0,
    val winTarget: Int = GameConstants.WIN_VALUE,
    val state: GameState = GameState.Playing,
    val history: List<HistoryState> = emptyList(),
    val undosRemaining: Int = GameConstants.MAX_UNDO,
    val moves: Int = 0,
    val gameTime: Long = 0L
)
