package dev.game2048.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class GameViewModel : ViewModel() {

    private val engine = GameEngine()
    private var isMoving = false

    private val _board = MutableStateFlow(emptyBoard())
    val board: StateFlow<List<List<Int>>> = _board.asStateFlow()

    private val _state = MutableStateFlow<GameState>(GameState.Playing)
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private var _keptPlaying = MutableStateFlow<Boolean>(false)
    var keptPlaying = _keptPlaying.asStateFlow()

    // We use an ArrayDeque to perform the undo operations
    private val history = ArrayDeque<HistoryState>()

    init {
        restart()
    }

    fun restart() {
        _keptPlaying.value = false
        engine.startGame()
        history.clear()
        _board.value = engine.board
        _state.value = GameState.Playing
        _score.value = engine.score
        isMoving = false
    }

    fun keepPlaying() {
        _keptPlaying.value = true
    }

    fun undo() {
        if (isMoving || history.isEmpty()) return
        val prevState = history.removeLast()
        engine.restore(prevState.board, prevState.score, prevState.hasWon)

        _board.value = engine.board
        _score.value = engine.score
        _state.value = GameState.Playing
    }

    fun move(direction: Direction) {
        if (isMoving || (_state.value != GameState.Playing && !_keptPlaying.value)) return

        viewModelScope.launch {
            isMoving = true

            // Saving the old board for further restoration
            val currentState = HistoryState(
                board = engine.board,
                score = engine.score,
                hasWon = engine.hasWon
            )

            if (engine.move(direction)) {
                // add the snapshot to our arrayDeque
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
            }

            isMoving = false
        }
    }

    private fun emptyBoard(): List<List<Int>> = List(GameConstants.GRID_SIZE) { List(GameConstants.GRID_SIZE) { 0 } }
}
