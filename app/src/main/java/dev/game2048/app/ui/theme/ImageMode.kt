package dev.game2048.app.ui.theme

import dev.game2048.app.R

data class ImageMode(
    val tile2: Int = R.drawable.lru,
    val tile4: Int = R.drawable.nantes,
    val tile8: Int = R.drawable.orleans,
    val tile16: Int = R.drawable.lyon,
    val tile32: Int = R.drawable.montpellier,
    val tile64: Int = R.drawable.paris,
    val tile128: Int = R.drawable.lille,
    val tile256: Int = R.drawable.bordeaux,
    val tile512: Int = R.drawable.saint_etienne,
    val tile1024: Int = R.drawable.kotlin,
    val tile2048: Int = R.drawable.compose,
    val tileSuper: Int = R.drawable.travail
) {
    fun image2048(value: Int): Int = when (value) {
        2 -> tile2
        4 -> tile4
        8 -> tile8
        16 -> tile16
        32 -> tile32
        64 -> tile64
        128 -> tile128
        256 -> tile256
        512 -> tile512
        1024 -> tile1024
        2048 -> tile2048
        else -> tileSuper
    }
}
