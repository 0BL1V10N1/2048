package dev.game2048.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_stats")
data class GameStatsEntity(
    @PrimaryKey val id: Int = 1,
    val bestScore: Int = 0,
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val topTile: Int = 0
)
