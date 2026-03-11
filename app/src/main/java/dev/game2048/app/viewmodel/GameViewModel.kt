package dev.game2048.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.game2048.app.data.local.database.*
import dev.game2048.app.domain.engine.GameEngine
import dev.game2048.app.domain.model.Direction
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.domain.model.HistoryState
import dev.game2048.app.utils.GameConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    private val engine = GameEngine()
    private var isMoving = false

    private val _board = MutableStateFlow(emptyBoard())
    val board: StateFlow<List<List<Int>>> = _board.asStateFlow()

    private val _state = MutableStateFlow<GameState>(GameState.Playing)
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _bestScore = MutableStateFlow(0)
    val bestScore: StateFlow<Int> = _bestScore.asStateFlow()

    private var _keptPlaying = MutableStateFlow(false)
    val keptPlaying = _keptPlaying.asStateFlow()

    private var history = ArrayDeque<HistoryState>()

    init {
        loadGame()
    }

    private fun loadGame() {
        val savedGame = dbHelper.loadGame()

        if (savedGame != null && savedGame.state != GameState.Over::class.simpleName) {
            engine.restore(savedGame.board, savedGame.score, savedGame.state == GameState.Won::class.simpleName)

            _board.value = savedGame.board
            _score.value = savedGame.score
            _bestScore.value = savedGame.bestScore
            _keptPlaying.value = savedGame.keptPlaying

            _state.value = when (savedGame.state) {
                GameState.Won::class.simpleName -> GameState.Won
                GameState.Over::class.simpleName -> GameState.Over
                else -> GameState.Playing
            }

            // JSON -> List -> ArrayDeque
            val historyList = try {
                Json.decodeFromString<List<HistoryState>>(savedGame.history)
            } catch (e: Exception) {
                emptyList<HistoryState>()
            }
            history = ArrayDeque(historyList)
        } else {
            if (savedGame != null) _bestScore.value = savedGame.bestScore
            restart()
        }
    }

    private fun saveGame() {
        if (_score.value > _bestScore.value) {
            _bestScore.value = _score.value
        }

        // ArrayDeque -> List -> JSON
        val historyJson = Json.encodeToString(history.toList())

        val savedGame = SavedGame(
            score = _score.value,
            bestScore = _bestScore.value,
            state = _state.value::class.simpleName ?: "Playing",
            keptPlaying = _keptPlaying.value,
            board = _board.value,
            history = historyJson
        )
        dbHelper.saveGame(savedGame)
    }

    fun restart() {
        _keptPlaying.value = false
        engine.startGame()
        history.clear()

        _board.value = engine.board
        _state.value = GameState.Playing
        _score.value = engine.score
        isMoving = false

        saveGame()
    }

    fun keepPlaying() {
        _keptPlaying.value = true
        saveGame()
    }

    fun undo() {
        if (isMoving || history.isEmpty()) return
        val prevState = history.removeLast()
        engine.restore(prevState.board, prevState.score, prevState.hasWon)

        _board.value = engine.board
        _score.value = engine.score
        _state.value = GameState.Playing

        saveGame()
    }

    fun move(direction: Direction) {
        if (isMoving || (_state.value != GameState.Playing && !_keptPlaying.value)) return

        viewModelScope.launch {
            isMoving = true

            val currentState = HistoryState(
                board = engine.board,
                score = engine.score,
                hasWon = engine.hasWon
            )

            if (engine.move(direction)) {
                history.addLast(currentState)
                if (history.size > GameConstants.MAX_HISTORY) {
                    history.removeFirst()
                }

                _board.value = engine.board
                delay(GameConstants.SPAWN_DELAY_MS)

                engine.spawnRandomTile()
                _board.value = engine.board
                _score.value = engine.score

                _state.value = when {
                    engine.isGameOver() -> GameState.Over
                    engine.hasWon && !_keptPlaying.value -> GameState.Won
                    else -> GameState.Playing
                }

                saveGame()
            }

            isMoving = false
        }
    }

    private fun emptyBoard(): List<List<Int>> = List(GameConstants.GRID_SIZE) { List(GameConstants.GRID_SIZE) { 0 } }
}
