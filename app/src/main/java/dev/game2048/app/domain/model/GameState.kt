package dev.game2048.app.domain.model

sealed interface GameState {
    data object Playing : GameState
    data object Won : GameState
    data object Over : GameState
}
