package org.example.project.ui.navigation

import SimpleDatePickerScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.example.project.ui.screens.ProductScreen
import org.example.project.ui.screens.UnderDevelopmentScreen
import org.example.project.ui.screens.ketua.DashboardScreen
import org.example.project.ui.screens.ketua.VerifikasiScreen
import org.example.project.ui.screens.user.UserDashboardScreen
import org.example.project.ui.screens.user.TambahLaporanScreen
import org.example.project.ui.screens.user.EditLaporanScreen
import org.example.project.ui.screens.ketua.FormSatuScreen
import org.example.project.ui.viewmodel.VerifikasiViewModel

sealed class Screen(val route: String) {
    object ProductList : Screen("product_list")
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
    NavHost(navController, startDestination = Screen.Kalendar.route) {
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
                onNavigateBack = { navController.popBackStack() }, // untuk kembali ke layar sebelumnya
                onSubmit = { formData ->
                    // TODO: Simpan data ke ViewModel, database, dsb.
                    println("Data yang dikirim: $formData")
                    navController.popBackStack() // setelah submit, kembali ke layar sebelumnya
                }
            )
        }
        composable(Screen.KirimLaporan.route) {
            TambahLaporanScreen(navController = navController)
        }
        composable(Screen.Kalendar.route) {
            SimpleDatePickerScreen(navController = navController)
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
