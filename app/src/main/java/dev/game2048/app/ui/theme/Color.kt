package dev.game2048.app.ui.theme

import androidx.compose.ui.graphics.Color

val GridBackground = Color(0xFFAD9D8F)
val TileEmpty = Color(0x99C1B3A4)
val GameTitle = Color(0xFF5C534B)

val ScoreText = Color(0xFF222222)

val HeaderButtons = Color(0xFFC73838)

val Tile2 = Color(0xFFEEE4DA)
val Tile4 = Color(0xFFEDE0C8)
val Tile8 = Color(0xFFF2B179)
val Tile16 = Color(0xFFF59563)
val Tile32 = Color(0xFFF67C5F)
val Tile64 = Color(0xFFF65E3B)
val Tile128 = Color(0xFFEDCF72)
val Tile256 = Color(0xFFEDCC61)
val Tile512 = Color(0xFFEDC850)
val Tile1024 = Color(0xFFEDC53F)
val Tile2048 = Color(0xFFEDC22E)
val TileSuper = Color(0xFF3C3A32)

val TextDark = Color(0xFF776E65)
val TextLight = Color(0xFFF9F6F2)

fun tileColor(value: Int): Color = when (value) {
    0 -> TileEmpty
    2 -> Tile2
    4 -> Tile4
    8 -> Tile8
    16 -> Tile16
    32 -> Tile32
    64 -> Tile64
    128 -> Tile128
    256 -> Tile256
    512 -> Tile512
    1024 -> Tile1024
    2048 -> Tile2048
    else -> TileSuper
}

fun tileTextColor(value: Int): Color = if (value <= 4) TextDark else TextLight
