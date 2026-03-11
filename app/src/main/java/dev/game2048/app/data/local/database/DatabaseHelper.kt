package dev.game2048.app.data.local.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.utils.GameConstants

data class SavedGame(
    val score: Int,
    val bestScore: Int,
    val state: String,
    val keptPlaying: Boolean,
    val board: List<List<Int>>,
    val history: String
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "Game2048.db", null, 1) {
    companion object {
        private const val TABLE_NAME = "GameSession"
        private const val ROW_ID = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                id INTEGER PRIMARY KEY,
                score INTEGER,
                best_score INTEGER,
                state TEXT,
                kept_playing INTEGER,
                board TEXT,
                history TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)

        val values = ContentValues().apply {
            put("id", ROW_ID)
            put("score", 0)
            put("best_score", 0)
            put("state", GameState.Over::class.simpleName) // over calls restart and spawn the random tiles
            put("kept_playing", 0)
            put("board", List(GameConstants.GRID_SIZE * GameConstants.GRID_SIZE) { 0 }.joinToString(","))
            put("history", "[]")
        }
        db.insert(TABLE_NAME, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun saveGame(savedGame: SavedGame) {
        val db = this.writableDatabase
        // Matrix is transformed to 1D array and then rebuilt
        val boardString = savedGame.board.flatten().joinToString(",")

        val values = ContentValues().apply {
            put("score", savedGame.score)
            put("best_score", savedGame.bestScore)
            put("state", savedGame.state)
            put("kept_playing", if (savedGame.keptPlaying) 1 else 0)
            put("board", boardString)
            put("history", savedGame.history)
        }

        db.update(TABLE_NAME, values, "id = ?", arrayOf(ROW_ID.toString()))
        db.close()
    }

    fun loadGame(): SavedGame? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NAME, null, "id = ?", arrayOf(ROW_ID.toString()), null, null, null)

        var game: SavedGame? = null

        if (cursor.moveToFirst()) {
            val score = cursor.getInt(cursor.getColumnIndexOrThrow("score"))
            val bestScore = cursor.getInt(cursor.getColumnIndexOrThrow("best_score"))
            val state = cursor.getString(cursor.getColumnIndexOrThrow("state"))
            val keptPlaying = cursor.getInt(cursor.getColumnIndexOrThrow("kept_playing")) == 1
            val boardString = cursor.getString(cursor.getColumnIndexOrThrow("board"))
            val history = cursor.getString(cursor.getColumnIndexOrThrow("history"))

            // with chunked we re-build the matrix
            val flatList = boardString.split(",").map { it.toInt() }
            val boardMatrix = flatList.chunked(GameConstants.GRID_SIZE)

            game = SavedGame(score, bestScore, state, keptPlaying, boardMatrix, history)
        }

        cursor.close()
        db.close()
        return game
    }
}
