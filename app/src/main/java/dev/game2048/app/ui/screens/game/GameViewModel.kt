package dev.game2048.app.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.game2048.app.data.repository.GameStateRepository
import dev.game2048.app.data.repository.SettingsRepository
import dev.game2048.app.data.repository.StatsRepository
import dev.game2048.app.domain.engine.GameEngine
import dev.game2048.app.domain.model.Direction
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.domain.model.HistoryState
import dev.game2048.app.utils.GameConstants
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameStateRepository: GameStateRepository,
    private val settingsRepository: SettingsRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {

    private var engine = GameEngine()
    private val history = ArrayDeque<HistoryState>()
    private var gridSize = GameConstants.GRID_SIZE

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadOrStartGame()
    }

    fun restart() {
        resetGame()
        saveGame()
    }

    fun continueGame() {
        engine.doubleWinTarget()
        _uiState.update {
            it.copy(
                winTarget = engine.winTarget,
                state = GameState.Playing
            )
        }
        saveGame()
    }

    fun move(direction: Direction) {
        val current = _uiState.value
        if (current.isMoving || current.state != GameState.Playing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isMoving = true) }

            val snapshot = HistoryState(
                board = engine.board,
                score = engine.score,
                winTarget = engine.winTarget
            )

            if (engine.move(direction)) {
                history.addLast(snapshot)
                if (history.size > current.undosRemaining) history.removeFirst()

                _uiState.update { it.copy(board = engine.board) }

                delay(GameConstants.SPAWN_DELAY_MS.milliseconds)

                engine.spawnRandomTile()

                val newState = when {
                    engine.isGameOver() -> GameState.Over
                    engine.hasWon -> GameState.Won
                    else -> GameState.Playing
                }

                val newScore = engine.score
                _uiState.update {
                    it.copy(
                        board = engine.board,
                        score = newScore,
                        bestScore = maxOf(newScore, it.bestScore),
                        winTarget = engine.winTarget,
                        state = newState
                    )
                }

                if (newState == GameState.Over || newState == GameState.Won) {
                    updateStats(newState)
                }

                saveGame()
            }

            _uiState.update { it.copy(isMoving = false) }
        }
    }

    fun undo() {
        if (_uiState.value.isMoving || history.isEmpty()) return

        val previous = history.removeLast()
        engine.restore(previous.board, previous.score, previous.winTarget)
        _uiState.update {
            it.copy(
                board = engine.board,
                score = engine.score,
                winTarget = engine.winTarget,
                state = GameState.Playing,
                undosRemaining = it.undosRemaining - 1
            )
        }
        saveGame()
    }

    private fun resetGame() {
        engine.startGame()
        history.clear()
        _uiState.update {
            it.copy(
                board = engine.board,
                score = engine.score,
                winTarget = engine.winTarget,
                state = GameState.Playing,
                undosRemaining = GameConstants.MAX_UNDO,
                isMoving = false
            )
        }
    }

    private fun loadOrStartGame() {
        viewModelScope.launch {
            statsRepository.load()
            gridSize = settingsRepository.getGridSize()
            engine = GameEngine(size = gridSize)

            val saved = gameStateRepository.load()
            val canRestore = saved != null &&
                saved.state != GameState.Over &&
                saved.board.size == gridSize

            val bestScore = statsRepository.stats.value.bestScore

            if (canRestore) {
                engine.restore(saved.board, saved.score, saved.winTarget)
                history.clear()
                history.addAll(saved.history)
                _uiState.value = saved.toUiState(bestScore)
            } else {
                _uiState.update { it.copy(bestScore = bestScore) }
                restart()
            }

            observeSettings()
        }
    }

    private suspend fun observeSettings() {
        settingsRepository.gridSizeFlow
            .distinctUntilChanged()
            .collect { newSize ->
                if (newSize != gridSize) {
                    gridSize = newSize
                    engine = GameEngine(size = newSize)
                    resetGame()
                    saveGame()
                }
            }
    }

    private fun updateStats(endState: GameState) {
        viewModelScope.launch {
            val current = statsRepository.stats.value
            val topTile = _uiState.value.board.maxOf { row -> row.max() }
            val updated = current.copy(
                bestScore = maxOf(current.bestScore, _uiState.value.score),
                gamesPlayed = current.gamesPlayed + 1,
                wins = current.wins + if (endState == GameState.Won) 1 else 0,
                losses = current.losses + if (endState == GameState.Over) 1 else 0,
                topTile = maxOf(current.topTile, topTile)
            )
            statsRepository.save(updated)
        }
    }

    private fun saveGame() {
        viewModelScope.launch {
            gameStateRepository.save(_uiState.value.toEntity(history.toList()))
        }
    }
}
