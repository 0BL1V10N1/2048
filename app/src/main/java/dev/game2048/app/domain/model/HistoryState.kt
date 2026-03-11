package dev.game2048.app.domain.model

data class HistoryState(val board: List<List<Int>>, val score: Int, val winTarget: Int)
