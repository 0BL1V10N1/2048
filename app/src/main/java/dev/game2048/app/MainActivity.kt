package dev.game2048.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.edit
import dagger.hilt.android.AndroidEntryPoint
import dev.game2048.app.domain.model.GameSettings
import dev.game2048.app.ui.screens.game.GameScreen
import dev.game2048.app.ui.theme.Game2048Theme
import dev.game2048.app.ui.theme.Theme
import dev.game2048.app.utils.MediaPlayer

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mediaPlayer = MediaPlayer()
    private val prefs by lazy { getSharedPreferences("game_prefs", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mediaPlayer.initMediaPlayer(this)

        val initialSettings = GameSettings(
            isSoundEnabled = prefs.getBoolean("sound_enabled", true),
            isAnimationEnabled = prefs.getBoolean("animation_enabled", true),
            isAccelerometerEnabled = prefs.getBoolean("sensor_enabled", false),
            currentTheme = Theme.valueOf(prefs.getString("theme_pref", Theme.LIGHT.name) ?: Theme.LIGHT.name)
        )

        setContent {
            var settings by remember { mutableStateOf(initialSettings) }

            Game2048Theme(themeType = settings.currentTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(
                        modifier = Modifier.padding(innerPadding),
                        settings = settings,
                        onSettingsChanged = { newSettings ->
                            if (newSettings.isSoundEnabled != settings.isSoundEnabled) {
                                if (newSettings.isSoundEnabled) {
                                    mediaPlayer.startMediaPlayer()
                                } else {
                                    mediaPlayer.pauseMediaPlayer()
                                }
                            }

                            prefs.edit {
                                putBoolean("sound_enabled", newSettings.isSoundEnabled)
                                putBoolean("animation_enabled", newSettings.isAnimationEnabled)
                                putBoolean("sensor_enabled", newSettings.isAccelerometerEnabled)
                                putString("theme_pref", newSettings.currentTheme.name)
                            }

                            settings = newSettings
                        }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (prefs.getBoolean("sound_enabled", true)) {
            mediaPlayer.startMediaPlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.pauseMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.destroyMediaPlayer()
    }
}
