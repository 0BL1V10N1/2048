package dev.game2048.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import dev.game2048.app.ui.dialogs.settings.SettingsDialog
import dev.game2048.app.ui.screens.game.GameScreen
import dev.game2048.app.ui.screens.game.GameViewModel
import dev.game2048.app.ui.screens.stats.StatsScreen
import dev.game2048.app.ui.screens.stats.StatsViewModel

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.Game) {
        composable<Route.Game> { backStackEntry ->
            val viewModel: GameViewModel = hiltViewModel(backStackEntry)

            GameScreen(
                modifier = modifier,
                viewModel = viewModel,
                onNavigateToStats = { navController.navigateSafe(Route.Stats) },
                onNavigateToSettings = { navController.navigateSafe(Route.Settings) }
            )
        }
        dialog<Route.Settings> {
            SettingsDialog(
                onDismiss = { navController.popBackSafe() },
                onApply = { navController.popBackSafe() }
            )
        }
        composable<Route.Stats> { backStackEntry ->
            val statsViewModel: StatsViewModel = hiltViewModel(backStackEntry)

            StatsScreen(
                modifier = modifier,
                viewModel = statsViewModel,
                onBack = { navController.popBackSafe() }
            )
        }
    }
}

private fun NavController.navigateSafe(route: Route) {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        navigate(route) { launchSingleTop = true }
    }
}

private fun NavController.popBackSafe() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}
