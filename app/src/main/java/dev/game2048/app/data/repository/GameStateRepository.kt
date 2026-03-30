package dev.game2048.app.data.repository

import dev.game2048.app.data.local.dao.GameStateDao
import dev.game2048.app.data.local.entity.GameStateEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameStateRepository @Inject constructor(private val dao: GameStateDao) {

    suspend fun load(): GameStateEntity? = dao.getGameState()

    suspend fun save(entity: GameStateEntity) = dao.saveGameState(entity)
}
