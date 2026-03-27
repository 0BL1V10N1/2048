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

        val defaultSoundState = prefs.getBoolean("sound_enabled", true)
        val defaultAnimationState = prefs.getBoolean("animation_enabled", true)
        val savedThemeName = prefs.getString("theme_pref", Theme.LIGHT.name) ?: Theme.LIGHT.name

        mediaPlayer.initMediaPlayer(this)

        setContent {
            var isSoundEnabled by remember { mutableStateOf(defaultSoundState) }
            var isAnimationEnabled by remember { mutableStateOf(defaultAnimationState) }
            var currentTheme by remember { mutableStateOf(Theme.valueOf(savedThemeName)) }

            Game2048Theme(themeType = currentTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(
                        modifier = Modifier.padding(innerPadding),
                        isSoundEnabled = isSoundEnabled,
                        onSoundToggled = { enabled ->
                            isSoundEnabled = enabled
                            prefs.edit { putBoolean("sound_enabled", enabled) }

                            if (enabled) {
                                mediaPlayer.startMediaPlayer()
                            } else {
                                mediaPlayer.pauseMediaPlayer()
                            }
                        },
                        currentTheme = currentTheme,
                        onThemeChanged = { newTheme ->
                            currentTheme = newTheme
                            prefs.edit { putString("theme_pref", newTheme.name) }
                        },
                        isAnimationEnabled = isAnimationEnabled,
                        onAnimationEnabled = { enabled ->
                            isAnimationEnabled = enabled
                            prefs.edit { putBoolean("animation_enabled", enabled) }
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
