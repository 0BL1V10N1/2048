package dev.game2048.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.game2048.app.domain.engine.GameEngine
import dev.game2048.app.domain.model.Direction
import dev.game2048.app.domain.model.GameState
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

    init {
        restart()
    }

    fun restart() {
        engine.startGame()
        _board.value = engine.board
        _state.value = GameState.Playing
        _score.value = 0
        isMoving = false
    }

    fun move(direction: Direction) {
        if (isMoving || _state.value != GameState.Playing) return

        viewModelScope.launch {
            isMoving = true

            if (engine.move(direction)) {
                _board.value = engine.board

                delay(GameConstants.SPAWN_DELAY_MS)

                engine.spawnRandomTile()
                _board.value = engine.board
                _score.value = engine.score

                _state.value = when {
                    engine.hasWon -> GameState.Won
                    engine.isGameOver() -> GameState.Over
                    else -> GameState.Playing
                }
            }

            isMoving = false
        }
    }

    private fun emptyBoard(): List<List<Int>> = List(GameConstants.GRID_SIZE) { List(GameConstants.GRID_SIZE) { 0 } }
}
