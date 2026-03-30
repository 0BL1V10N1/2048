package dev.game2048.app.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Game : Route

    @Serializable data object Settings : Route

    @Serializable data object Stats : Route
}
