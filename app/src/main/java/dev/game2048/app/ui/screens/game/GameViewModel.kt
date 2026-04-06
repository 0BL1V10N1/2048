package dev.game2048.app.ui.screens.game

import android.content.Context
import android.media.SoundPool
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.game2048.app.R
import dev.game2048.app.data.repository.GameStateRepository
import dev.game2048.app.data.repository.SettingsRepository
import dev.game2048.app.data.repository.StatsRepository
import dev.game2048.app.domain.engine.GameEngine
import dev.game2048.app.domain.model.Direction
import dev.game2048.app.domain.model.GameSettings
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.domain.model.HistoryState
import dev.game2048.app.utils.GameConstants
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GameViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val gameStateRepository: GameStateRepository,
    private val settingsRepository: SettingsRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {
    private var audioPlayer: SoundPool = SoundPool.Builder().setMaxStreams(2).build()
    var mergeFailId: Int = 0
    var mergeSuccessId: Int = 0

    val settings: StateFlow<GameSettings> = settingsRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GameSettings())
    private val history = ArrayDeque<HistoryState>()
    private var gridSize = GameConstants.GRID_SIZE

    private var engine = GameEngine(gridSize)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadSongs()
        loadOrStartGame()
    }

    fun restart() {
        if (_uiState.value.state == GameState.Playing && _uiState.value.score > 0) {
            updateStats(GameState.Over)
        }

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
            engine.hasMerged = false

            val snapshot = HistoryState(
                board = engine.board,
                score = engine.score,
                winTarget = engine.winTarget
            )

            if (engine.move(direction)) {
                _uiState.update { it.copy(moves = it.moves + 1) }
                history.addLast(snapshot)
                if (history.size > current.undosRemaining) history.removeFirst()

                playSound(engine.hasMerged)
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
                isMoving = false,
                moves = 0
            )
        }
    }

    private fun loadSongs() {
        mergeFailId = audioPlayer.load(context, R.raw.merge_failed, 1)
        mergeSuccessId = audioPlayer.load(context, R.raw.merge_success, 1)
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
            val finalScore = _uiState.value.score

            val newTopScores = (current.topScores + finalScore)
                .sortedDescending()
                .take(5)

            val updated = current.copy(
                gamesPlayed = current.gamesPlayed + 1,
                wins = current.wins + if (endState == GameState.Won) 1 else 0,
                losses = current.losses + if (endState == GameState.Over) 1 else 0,
                topScores = newTopScores
            )
            statsRepository.save(updated)
        }
    }

    private fun saveGame() {
        viewModelScope.launch {
            gameStateRepository.save(_uiState.value.toEntity(history.toList()))

            val currentStats = statsRepository.stats.value
            val topTile = _uiState.value.board.maxOf { row ->
                row.maxOfOrNull { tile -> tile?.value ?: 0 } ?: 0
            }

            if (_uiState.value.score > currentStats.bestScore || topTile > currentStats.topTile) {
                val updatedStats = currentStats.copy(
                    bestScore = maxOf(currentStats.bestScore, _uiState.value.score),
                    topTile = maxOf(currentStats.topTile, topTile)
                )
                statsRepository.save(updatedStats)
            }
        }
    }

    private fun playSound(hasMerged: Boolean) {
        val soundId = if (hasMerged) mergeSuccessId else mergeFailId
        if (soundId != 0) {
            audioPlayer.play(soundId, 1f, 1f, 0, 0, 1f)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
