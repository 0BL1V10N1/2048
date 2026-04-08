package dev.game2048.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.game2048.app.domain.model.GameSettings
import dev.game2048.app.ui.theme.Theme
import dev.game2048.app.utils.GameConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val GRID_SIZE = intPreferencesKey("grid_size")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val ANIMATION_ENABLED = booleanPreferencesKey("animation_enabled")
        val SENSOR_ENABLED = booleanPreferencesKey("sensor_enabled")
        val CURRENT_THEME = stringPreferencesKey("theme_pref")
        val IMAGE_MODE = booleanPreferencesKey("image_enabled")
    }

    val gameSettingsFlow: Flow<GameSettings> =
        context.settingsDataStore.data.map { prefs ->
            GameSettings(
                isSoundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
                currentTheme = Theme.valueOf(prefs[Keys.CURRENT_THEME] ?: Theme.SYSTEM.name),
                isAccelerometerEnabled = prefs[Keys.SENSOR_ENABLED] ?: false,
                isAnimationEnabled = prefs[Keys.ANIMATION_ENABLED] ?: true,
                usesImage = prefs[Keys.IMAGE_MODE] ?: false,
                gridSize = prefs[Keys.GRID_SIZE] ?: GameConstants.GRID_SIZE
            )
        }

    suspend fun saveSettings(settings: GameSettings) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.SOUND_ENABLED] = settings.isSoundEnabled
            prefs[Keys.CURRENT_THEME] = settings.currentTheme.toString()
            prefs[Keys.ANIMATION_ENABLED] = settings.isAnimationEnabled
            prefs[Keys.SENSOR_ENABLED] = settings.isAccelerometerEnabled
            prefs[Keys.IMAGE_MODE] = settings.usesImage
            prefs[Keys.GRID_SIZE] = settings.gridSize
        }
    }
}
