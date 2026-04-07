package dev.game2048.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.game2048.app.data.repository.SettingsRepository
import dev.game2048.app.domain.model.GameSettings
import dev.game2048.app.ui.navigation.AppNavHost
import dev.game2048.app.ui.theme.Game2048Theme
import dev.game2048.app.utils.MediaPlayer
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mediaPlayer = MediaPlayer()

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mediaPlayer.initMediaPlayer(this)

        setContent {
            val settings by settingsRepository.settingsFlow.collectAsState(initial = GameSettings())

            LaunchedEffect(settings.isSoundEnabled) {
                if (settings.isSoundEnabled) {
                    mediaPlayer.startMediaPlayer()
                } else {
                    mediaPlayer.pauseMediaPlayer()
                }
            }

            Game2048Theme(themeType = settings.currentTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
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
