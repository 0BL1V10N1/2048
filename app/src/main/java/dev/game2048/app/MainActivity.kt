package dev.game2048.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dagger.hilt.android.AndroidEntryPoint
import dev.game2048.app.data.repository.SettingsRepository
import dev.game2048.app.domain.model.GameSettings
import dev.game2048.app.ui.navigation.AppNavHost
import dev.game2048.app.ui.theme.Game2048Theme
import dev.game2048.app.utils.MusicPlayer
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val musicPlayer = MusicPlayer()

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        musicPlayer.init(this)

        setContent {
            val settings by settingsRepository.settingsFlow.collectAsState(
                initial = GameSettings(isMusicEnabled = false)
            )
            val lifecycleOwner = LocalLifecycleOwner.current

            DisposableEffect(lifecycleOwner, settings.isMusicEnabled) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> {
                            if (settings.isMusicEnabled) {
                                musicPlayer.play()
                            }
                        }

                        Lifecycle.Event.ON_PAUSE -> {
                            musicPlayer.pause()
                        }

                        Lifecycle.Event.ON_DESTROY -> {
                            musicPlayer.release()
                        }

                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)

                if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    if (settings.isMusicEnabled) {
                        musicPlayer.play()
                    } else {
                        musicPlayer.pause()
                    }
                }

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            Game2048Theme(themeType = settings.currentTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
