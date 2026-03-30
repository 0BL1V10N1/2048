package dev.game2048.app.data.repository

import dev.game2048.app.data.local.dao.StatsDao
import dev.game2048.app.data.local.entity.GameStatsEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class StatsRepository @Inject constructor(private val dao: StatsDao) {

    private val _stats = MutableStateFlow(GameStatsEntity())
    val stats: StateFlow<GameStatsEntity> = _stats.asStateFlow()

    suspend fun load() {
        _stats.value = dao.getStats() ?: GameStatsEntity()
    }

    suspend fun save(entity: GameStatsEntity) {
        _stats.value = entity
        dao.saveStats(entity)
    }
}
