package dev.game2048.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.domain.model.HistoryState

@Entity(tableName = "game_state")
data class GameEntity(
    @PrimaryKey val id: Int = 1,
    val board: List<List<Int>>,
    val score: Int,
    val bestScore: Int,
    val winTarget: Int,
    val state: GameState,
    val history: List<HistoryState>
)
