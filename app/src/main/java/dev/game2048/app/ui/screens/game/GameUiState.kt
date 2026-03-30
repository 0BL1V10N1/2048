package dev.game2048.app.ui.screens.game

import dev.game2048.app.data.local.entity.GameStateEntity
import dev.game2048.app.domain.model.GameState
import dev.game2048.app.domain.model.HistoryState
import dev.game2048.app.utils.GameConstants

data class GameUiState(
    val board: List<List<Int>> = List(GameConstants.GRID_SIZE) { List(GameConstants.GRID_SIZE) { 0 } },
    val score: Int = 0,
    val bestScore: Int = 0,
    val winTarget: Int = GameConstants.WIN_VALUE,
    val state: GameState = GameState.Playing,
    val undosRemaining: Int = GameConstants.MAX_UNDO,
    val isMoving: Boolean = false
)

fun GameStateEntity.toUiState(bestScore: Int): GameUiState = GameUiState(
    board = board,
    score = score,
    bestScore = bestScore,
    winTarget = winTarget,
    state = state,
    undosRemaining = undosRemaining,
    isMoving = false
)

fun GameUiState.toEntity(history: List<HistoryState>): GameStateEntity = GameStateEntity(
    board = board,
    score = score,
    winTarget = winTarget,
    state = state,
    history = history,
    undosRemaining = undosRemaining
)
