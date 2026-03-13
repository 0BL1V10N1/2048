package dev.game2048.app.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.game2048.app.data.local.entity.GameEntity
import dev.game2048.app.data.repository.GameRepository
import dev.game2048.app.domain.engine.GameEngine
import dev.game2048.app.domain.model.Direction
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.domain.model.HistoryState
import dev.game2048.app.utils.GameConstants
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GameViewModel @Inject constructor(private val repository: GameRepository) : ViewModel() {
    private var isMoving = false

    private var currentGridSize = GameConstants.GRID_SIZE
    private var engine = GameEngine(currentGridSize)
    private val history = ArrayDeque<HistoryState>()

    private val _board = MutableStateFlow(emptyBoard())
    val board: StateFlow<List<List<Int>>> = _board.asStateFlow()

    private val _state = MutableStateFlow<GameState>(GameState.Playing)
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _bestScore = MutableStateFlow(0)
    val bestScore: StateFlow<Int> = _bestScore.asStateFlow()

    private val _winTarget = MutableStateFlow(GameConstants.WIN_VALUE)
    val winTarget: StateFlow<Int> = _winTarget.asStateFlow()

    init {
        loadOrStartGame()
    }

    fun restart(size: Int = currentGridSize) {
        currentGridSize = size
        engine = GameEngine(currentGridSize)

        engine.startGame()
        history.clear()
        syncUi()
        _state.value = GameState.Playing
        isMoving = false

        saveGame()
    }

    fun continueGame() {
        engine.doubleWinTarget()
        _winTarget.value = engine.winTarget
        _state.value = GameState.Playing

        saveGame()
    }

    fun move(direction: Direction) {
        if (isMoving || _state.value != GameState.Playing) return

        viewModelScope.launch {
            isMoving = true

            val snapshot = HistoryState(
                board = engine.board,
                score = engine.score,
                winTarget = engine.winTarget
            )

            if (engine.move(direction)) {
                history.addLast(snapshot)
                if (history.size > GameConstants.MAX_HISTORY) {
                    history.removeFirst()
                }

                _board.value = engine.board

                delay(GameConstants.SPAWN_DELAY_MS)

                engine.spawnRandomTile()
                _board.value = engine.board
                _score.value = engine.score
                updateBestScore()

                _state.value = when {
                    engine.isGameOver() -> GameState.Over
                    engine.hasWon -> GameState.Won
                    else -> GameState.Playing
                }

                saveGame()
            }

            isMoving = false
        }
    }

    fun undo() {
        if (isMoving || history.isEmpty()) return
        val prevState = history.removeLast()
        engine.restore(prevState.board, prevState.score, prevState.winTarget)

        syncUi()
        _state.value = GameState.Playing

        saveGame()
    }

    private fun loadOrStartGame() {
        viewModelScope.launch {
            val saved = repository.loadGame()
            if (saved != null && saved.state != GameState.Over) {
                currentGridSize = saved.board.size
                engine = GameEngine(currentGridSize)
                engine.restore(saved.board, saved.score, saved.winTarget)
                history.clear()
                history.addAll(saved.history)

                syncUi()
                _bestScore.value = saved.bestScore
                _state.value = saved.state
            } else {
                _bestScore.value = saved?.bestScore ?: 0
                restart(currentGridSize)
            }
        }
    }

    private fun saveGame() {
        viewModelScope.launch {
            repository.saveGame(
                GameEntity(
                    board = engine.board,
                    score = engine.score,
                    bestScore = _bestScore.value,
                    winTarget = engine.winTarget,
                    state = _state.value,
                    history = history.toList()
                )
            )
        }
    }

    private fun updateBestScore() {
        _bestScore.value = maxOf(_bestScore.value, _score.value)
    }

    private fun syncUi() {
        _board.value = engine.board
        _score.value = engine.score
        _winTarget.value = engine.winTarget
    }

    private fun emptyBoard(): List<List<Int>> = List(currentGridSize) { List(currentGridSize) { 0 } }
}
