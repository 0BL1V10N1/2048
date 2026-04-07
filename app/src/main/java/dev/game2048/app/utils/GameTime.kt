package dev.game2048.app.utils

import java.lang.String.format

fun formatGameTime(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return format("%02d:%02d", minutes, seconds)
}
