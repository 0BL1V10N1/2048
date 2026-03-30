package dev.game2048.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.game2048.app.utils.GameConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val GRID_SIZE = intPreferencesKey("grid_size")
    }

    val gridSizeFlow: Flow<Int> = context.settingsDataStore.data.map { prefs ->
        prefs[Keys.GRID_SIZE] ?: GameConstants.GRID_SIZE
    }

    suspend fun saveGridSize(size: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.GRID_SIZE] = size
        }
    }
}
