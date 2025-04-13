package org.example.project.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.example.project.ui.screens.ProductScreen
import org.example.project.ui.screens.ketua.DashboardScreen
import org.example.project.ui.screens.ketua.VerifikasiScreen
import org.example.project.ui.screens.ketua.FormSatuScreen
import org.example.project.ui.viewmodel.shared.SharedReportViewModel

sealed class Screen(val route: String) {
    object ProductList : Screen("product_list")
    object DashboardKetua : Screen("dashboard_ketua")
    object VerifikasiKasus : Screen("verifikasi_kasus")
    object FormPelaporan : Screen("form_pelaporan") // ⬅ Tambahkan ini
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun navGraph(navController: NavHostController, sharedReportViewModel: SharedReportViewModel) {
    NavHost(navController, startDestination = Screen.DashboardKetua.route) {
        composable(Screen.ProductList.route) {
            ProductScreen()
        }
        composable(Screen.DashboardKetua.route) {
            DashboardScreen(navController, sharedViewModel = sharedReportViewModel)
        }
        composable(Screen.VerifikasiKasus.route) {
            VerifikasiScreen(navController, sharedViewModel = sharedReportViewModel)
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
    }
}