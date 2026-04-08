package dev.game2048.app.ui.theme

import dev.game2048.app.R

object TileImages {
    private val images = mapOf(
        2 to R.drawable.lru,
        4 to R.drawable.nantes,
        8 to R.drawable.orleans,
        16 to R.drawable.lyon,
        32 to R.drawable.montpellier,
        64 to R.drawable.paris,
        128 to R.drawable.lille,
        256 to R.drawable.bordeaux,
        512 to R.drawable.saint_etienne,
        1024 to R.drawable.kotlin,
        2048 to R.drawable.compose
    )

    private val fallback = R.drawable.work

    fun forValue(value: Int): Int = images[value] ?: fallback
}
