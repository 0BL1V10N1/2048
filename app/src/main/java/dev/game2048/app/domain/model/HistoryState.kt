package dev.game2048.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HistoryState(val board: List<List<Int>>, val score: Int, val winTarget: Int)
