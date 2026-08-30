package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.screens.admin.AdminDashboardScreen
import com.example.screens.driver.WalletScreen
import com.example.screens.driver.DriverOrderListScreen
import com.example.screens.home.HomeScreen
import com.example.viewmodels.AdminDashboardViewModel
import com.example.viewmodels.HomeViewModel
import com.example.viewmodels.WalletViewModel
import com.example.viewmodels.OrderViewModel
import com.example.repository.WalletRepository
import com.example.repository.OrderRepository

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(homeViewModel)
        }
        composable(Routes.ADMIN_DASHBOARD) {
            val adminViewModel: AdminDashboardViewModel = viewModel()
            AdminDashboardScreen(usersList = mutableListOf(), ordersList = emptyList(), onBack = {}, onLogout = {}, viewModel = adminViewModel)
        }
        composable(Routes.WALLET) { backStackEntry ->
            val driverId = backStackEntry.arguments?.getString("driverId") ?: ""
            val walletViewModel = WalletViewModel(WalletRepository())
            WalletScreen(walletViewModel, driverId)
        }
        composable(Routes.DRIVER_ORDERS) { backStackEntry ->
            val driverId = backStackEntry.arguments?.getString("driverId") ?: ""
            val orderViewModel: OrderViewModel = viewModel(factory = OrderViewModel.Factory)
            DriverOrderListScreen(orderViewModel, driverId, onNavigateToWallet = {
                navController.navigate(Routes.WALLET.replace("{driverId}", driverId))
            })
        }
    }
}
