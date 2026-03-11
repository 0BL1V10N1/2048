package dev.game2048.app.ui.theme

import androidx.compose.ui.unit.sp

fun formatTextValues(value: Int): String = when {
    value >= 1000000 -> "${value / 1000000}M"
    value >= 10000 -> "${value / 1000}K"
    else -> value.toString()
}

fun tileFontSize(value: String) = when (value.length) {
    1, 2 -> 36.sp
    3 -> 30.sp
    4 -> 24.sp
    else -> 20.sp
}
