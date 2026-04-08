package dev.game2048.app.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import dev.game2048.app.R
import java.io.IOException

class MusicPlayer {
    private var player: MediaPlayer? = null

    fun init(context: Context) {
        if (player != null) return
        try {
            player = MediaPlayer.create(context, R.raw.music)?.apply {
                isLooping = true
            }
        } catch (e: IOException) {
            Log.e("MusicPlayer", "Failed to create MediaPlayer", e)
        }
    }

    fun play() {
        player?.takeIf { !it.isPlaying }?.start()
    }

    fun pause() {
        player?.takeIf { it.isPlaying }?.pause()
    }

    fun release() {
        player?.release()
        player = null
    }
}
