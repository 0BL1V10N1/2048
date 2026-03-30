package dev.game2048.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.game2048.app.data.local.entity.GameStatsEntity

@Dao
interface StatsDao {

    @Query("SELECT * FROM game_stats WHERE id = 1")
    suspend fun getStats(): GameStatsEntity?

    @Upsert
    suspend fun saveStats(entity: GameStatsEntity)
}
