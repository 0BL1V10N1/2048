package dev.game2048.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
                onNavigateToStats = { navController.navigate(Route.Stats) },
                onNavigateToSettings = { navController.navigate(Route.Settings) }
            )
        }
        dialog<Route.Settings> {
            SettingsDialog(
                onDismiss = { navController.popBackStack() },
                onApply = { navController.popBackStack() }
            )
        }
        composable<Route.Stats> { backStackEntry ->
            val statsViewModel: StatsViewModel = hiltViewModel(backStackEntry)

            StatsScreen(
                modifier = modifier,
                viewModel = statsViewModel,
                onBack = { navController.popBackStack() },
                onReset = { statsViewModel.resetStats() }
            )
        }
    }
}
