package dev.game2048.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.game2048.app.data.local.entity.GameStateEntity

@Dao
interface GameStateDao {

    @Query("SELECT * FROM game_state WHERE id = 1")
    suspend fun getGameState(): GameStateEntity?

    @Upsert
    suspend fun saveGameState(entity: GameStateEntity)
}
