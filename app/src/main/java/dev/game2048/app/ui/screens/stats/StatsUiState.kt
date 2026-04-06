package dev.game2048.app.ui.screens.stats

import dev.game2048.app.data.local.entity.GameStatsEntity

data class StatsUiState(
    val bestScore: Int = 0,
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val topTile: Int = 0,
    val topScores: List<Int> = emptyList()
)

fun GameStatsEntity.toUiState(): StatsUiState = StatsUiState(
    bestScore = bestScore,
    gamesPlayed = gamesPlayed,
    wins = wins,
    losses = losses,
    topTile = topTile,
    topScores = topScores
)
