package org.example.project.ui.navigation

import SimpleDatePickerScreen
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.example.project.ui.screens.LoginScreen
import org.example.project.ui.screens.ProductScreen
import org.example.project.ui.screens.UnderDevelopmentScreen
import org.example.project.ui.screens.OnboardingScreen
import org.example.project.ui.screens.ketua.DashboardScreen
import org.example.project.ui.screens.ketua.FormSatuScreen
import org.example.project.ui.screens.ketua.VerifikasiScreen
import org.example.project.ui.screens.user.UserDashboardScreen
import org.example.project.ui.screens.user.TambahLaporanScreen
import org.example.project.ui.screens.user.EditLaporanScreen
import org.example.project.ui.viewmodel.AuthViewModel
import org.example.project.ui.viewmodel.VerifikasiViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object ProductList : Screen("product_list")
    object Login : Screen("Login")
    object DashboardKetua : Screen("dashboard_ketua")
    object VerifikasiKasus : Screen("verifikasi_kasus")
    object FormPelaporan : Screen("form_pelaporan")
    object KirimLaporan : Screen("kirim-laporan")
    object DashboardUser : Screen("dashboard_user")
    object EditLaporan : Screen("edit_laporan/{id}")
    object UnderDev : Screen("under_development")
    object Kalendar : Screen("kalendar")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun navGraph(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SrikandiAppPrefs", Context.MODE_PRIVATE)
    val isFirstLaunch = sharedPref.getBoolean("isFirstLaunch", true)

    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()
    val isAuthChecked by authViewModel.isAuthChecked.collectAsState()

    if (!isAuthChecked) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = when {
        isFirstLaunch -> Screen.Onboarding.route
        isLoggedIn && userRole != null -> when (userRole) {
            "Ketua Satgas" -> Screen.DashboardKetua.route
            "Satgas" -> Screen.UnderDev.route
            "User" -> Screen.DashboardUser.route
            else -> Screen.Login.route
        }
        else -> Screen.Login.route
    }
    NavHost(navController, startDestination = startDestination) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    with(sharedPref.edit()) {
                        putBoolean("isFirstLaunch", false)
                        apply()
                    }
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ProductList.route) {
            ProductScreen()
        }
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
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
            VerifikasiScreen(navController, reportviewModel = hiltViewModel(), verifikasiViewModel)
        }
        composable(Screen.UnderDev.route) {
            UnderDevelopmentScreen()
        }

        composable("feature_screen/{featureId}") { backStackEntry ->
            val featureId = backStackEntry.arguments?.getString("featureId") ?: "Unknown"
            Text("Feature Screen with ID: $featureId")
        }
        composable("notification_screen/{data}") { backStackEntry ->
            val data = backStackEntry.arguments?.getString("data") ?: "No Data"
            Text("Notification Data: $data")
        }

        composable(Screen.FormPelaporan.route) {
            FormSatuScreen(
                onNavigateBack = { navController.popBackStack() },
                onSubmit = { formData ->
                    println("Data yang dikirim: $formData")
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.KirimLaporan.route) {
            TambahLaporanScreen(navController = navController)
        }
        composable(Screen.Kalendar.route) {
            SimpleDatePickerScreen()
        }
        composable(Screen.DashboardUser.route) {
            UserDashboardScreen(navController)
        }
        composable(
            route = Screen.EditLaporan.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getInt("id") ?: return@composable
            EditLaporanScreen(navController = navController, reportId = reportId)
        }
    }
}