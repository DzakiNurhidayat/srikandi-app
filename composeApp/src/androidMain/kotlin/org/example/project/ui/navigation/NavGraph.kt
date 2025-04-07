package org.example.project.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.example.project.ui.screens.ProductScreen
import org.example.project.ui.screens.UnderDevelopmentScreen
import org.example.project.ui.screens.ketua.DashboardScreen
import org.example.project.ui.screens.ketua.VerifikasiScreen
import org.example.project.ui.viewmodel.VerifikasiViewModel

sealed class Screen(val route: String) {
    object ProductList : Screen("product_list")
    object DashboardKetua : Screen("dashboard_ketua")
    object VerifikasiKasus : Screen("verifikasi_kasus")
    object UnderDev : Screen("under_development")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun navGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.DashboardKetua.route) {
        composable(Screen.ProductList.route) {
            ProductScreen()
        }
        composable(Screen.DashboardKetua.route) {
            val verifikasiViewModel = hiltViewModel<VerifikasiViewModel>()
            DashboardScreen(navController, verifikasiViewModel = verifikasiViewModel)
        }
        composable(Screen.VerifikasiKasus.route) {
            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Screen.DashboardKetua.route)
            }
            val verifikasiViewModel = hiltViewModel<VerifikasiViewModel>(parentEntry)
            VerifikasiScreen(navController, verifikasiViewModel)
        }
        composable(Screen.UnderDev.route) {
            UnderDevelopmentScreen()
        }
    }
}

