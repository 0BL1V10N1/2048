package dev.game2048.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.game2048.app.data.models.Direction
import dev.game2048.app.data.models.GameState
import dev.game2048.app.data.sources.GameEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val engine = GameEngine()
    private var isMoving = false

    private val _state = MutableStateFlow<GameState>(GameState.Playing)
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val _board = MutableStateFlow(emptyBoard())
    val board: StateFlow<List<List<Int>>> = _board.asStateFlow()

    init {
        restart()
    }

    fun restart() {
        engine.startGame()
        _board.value = engine.board
        _state.value = GameState.Playing
        isMoving = false
    }

    fun onMove(direction: Direction) {
        if (isMoving || _state.value != GameState.Playing) return

        viewModelScope.launch {
            isMoving = true

            if (engine.move(direction)) {
                _board.value = engine.board

                delay(SPAWN_DELAY_MS)

                engine.spawnRandomTile()
                _board.value = engine.board

                _state.value = when {
                    engine.hasWon -> GameState.Won
                    engine.isGameOver() -> GameState.Over
                    else -> GameState.Playing
                }
            }

            isMoving = false
        }
    }

    private companion object {
        const val GRID_SIZE = 4
        const val SPAWN_DELAY_MS = 80L

        fun emptyBoard(): List<List<Int>> = List(GRID_SIZE) { List(GRID_SIZE) { 0 } }
    }
}
