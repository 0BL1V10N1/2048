package dev.game2048.app.ui.screens

import androidx.lifecycle.ViewModel
import dev.game2048.app.data.Direction
import dev.game2048.app.data.GameEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {

    private val engine = GameEngine(GRID_SIZE)

    private val _board = MutableStateFlow(emptyBoard())
    val board: StateFlow<List<List<Int>>> = _board.asStateFlow()

    init {
        engine.startGame()
        _board.value = engine.board()
    }

    fun onMove(direction: Direction) {
        engine.move(direction)
        _board.value = engine.board()
    }

    private companion object {
        const val GRID_SIZE = 4

        fun emptyBoard(): List<List<Int>> = List(GRID_SIZE) { List(GRID_SIZE) { 0 } }
    }
}
