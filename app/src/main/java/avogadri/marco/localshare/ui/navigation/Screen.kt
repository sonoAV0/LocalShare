package avogadri.marco.localshare.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Discovery : Screen("discovery")
}
