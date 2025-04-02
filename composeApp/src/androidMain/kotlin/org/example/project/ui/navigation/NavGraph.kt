package org.example.project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.example.project.ui.screens.ProductScreen
import org.example.project.ui.screens.ketua.DashboardScreen
import org.example.project.ui.screens.ketua.VerifikasiScreen

sealed class Screen(val route: String) {
    object ProductList : Screen("product_list")
    object DashboardKetua : Screen("dashboard_ketua")
    object VerifikasiKasus : Screen("verifikasi_kasus/{id}")
}

@Composable
fun navGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.DashboardKetua.route) {
        composable(Screen.ProductList.route) {
            ProductScreen()
        }
        composable(Screen.DashboardKetua.route) {
            DashboardScreen(navController)
        }
        composable(Screen.VerifikasiKasus.route) {
            VerifikasiScreen(id, navController)
        }
    }
}
