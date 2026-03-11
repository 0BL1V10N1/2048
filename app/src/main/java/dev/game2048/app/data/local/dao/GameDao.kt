package dev.game2048.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.game2048.app.data.local.entity.GameEntity

@Dao
interface GameDao {
    @Query("SELECT * FROM game_state WHERE id = 1")
    suspend fun getGameState(): GameEntity?

    @Upsert
    suspend fun saveGameState(entity: GameEntity)
}
