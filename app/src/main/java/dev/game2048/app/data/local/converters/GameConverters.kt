package dev.game2048.app.data.local.converters

import androidx.room.TypeConverter
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.domain.model.HistoryState
import dev.game2048.app.domain.model.Tile
import kotlinx.serialization.json.Json

class GameConverters {
    @TypeConverter
    fun fromBoard(board: List<List<Tile?>>): String = Json.encodeToString(board)

    @TypeConverter
    fun toBoard(json: String): List<List<Tile?>> = Json.decodeFromString(json)

    @TypeConverter
    fun fromIntList(list: List<Int>): String = Json.encodeToString(list)

    @TypeConverter
    fun toIntList(json: String): List<Int> = Json.decodeFromString(json)

    @TypeConverter
    fun fromGameState(state: GameState): String = when (state) {
        GameState.Playing -> "Playing"
        GameState.Won -> "Won"
        GameState.Over -> "Over"
    }

    @TypeConverter
    fun toGameState(value: String): GameState = when (value) {
        "Won" -> GameState.Won
        "Over" -> GameState.Over
        else -> GameState.Playing
    }

    @TypeConverter
    fun fromHistory(history: List<HistoryState>): String = Json.encodeToString(history)

    @TypeConverter
    fun toHistory(json: String): List<HistoryState> = Json.decodeFromString(json)
}
