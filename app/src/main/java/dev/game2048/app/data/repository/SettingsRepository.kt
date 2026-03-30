package dev.game2048.app.data.repository

import dev.game2048.app.data.local.datastore.SettingsDataStore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Singleton
class SettingsRepository @Inject constructor(private val dataStore: SettingsDataStore) {

    val gridSizeFlow: Flow<Int> = dataStore.gridSizeFlow

    suspend fun getGridSize(): Int = dataStore.gridSizeFlow.first()

    suspend fun saveGridSize(size: Int) = dataStore.saveGridSize(size)
}
