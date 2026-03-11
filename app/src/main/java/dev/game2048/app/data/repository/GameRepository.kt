package dev.game2048.app.data.repository

import dev.game2048.app.data.local.dao.GameDao
import dev.game2048.app.data.local.entity.GameEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(private val gameDao: GameDao) {
    suspend fun saveGame(entity: GameEntity) = gameDao.saveGameState(entity)

    suspend fun loadGame(): GameEntity? = gameDao.getGameState()

    suspend fun deleteGame() = gameDao.deleteGameState()
}
