package avogadri.marco.localshare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import avogadri.marco.localshare.ui.discovery.DiscoveryScreen
import avogadri.marco.localshare.ui.history.HistoryScreen
import avogadri.marco.localshare.ui.home.HomeScreen
import avogadri.marco.localshare.ui.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartTransfer = { navController.navigate(Screen.Discovery.route) },
                onOpenHistory = { navController.navigate(Screen.History.route) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
            )
        }
        composable(Screen.Discovery.route) {
            DiscoveryScreen()
        }
        composable(Screen.History.route) {
            HistoryScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
