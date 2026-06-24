package avogadri.marco.localshare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import avogadri.marco.localshare.ui.discovery.DiscoveryScreen
import avogadri.marco.localshare.ui.home.HomeScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartTransfer = { navController.navigate(Screen.Discovery.route) },
            )
        }
        composable(Screen.Discovery.route) {
            DiscoveryScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
