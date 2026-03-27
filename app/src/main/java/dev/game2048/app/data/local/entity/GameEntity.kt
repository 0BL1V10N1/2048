package dev.game2048.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.domain.model.HistoryState
import dev.game2048.app.domain.model.Tile

@Entity(tableName = "game_state")
data class GameEntity(
    @PrimaryKey val id: Int = 1,
    val board: List<List<Tile?>>,
    val score: Int,
    val winTarget: Int,
    val state: GameState,
    val history: List<HistoryState>
)
