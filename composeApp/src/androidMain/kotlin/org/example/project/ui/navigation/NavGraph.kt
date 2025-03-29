package org.example.project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.example.project.ui.screens.ProductScreen

sealed class Screen(val route: String) {
    object ProductList : Screen("product_list")
}

@Composable
fun navGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.ProductList.route) {
        composable(Screen.ProductList.route) {
            ProductScreen()
        }
    }
}


