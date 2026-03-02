package dev.game2048.app.data.models

sealed interface GameState {
    data object Playing : GameState
    data object Won : GameState
    data object Over : GameState
}
