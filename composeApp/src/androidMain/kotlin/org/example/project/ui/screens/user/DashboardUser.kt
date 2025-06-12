package org.example.project.ui.screens.user

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.example.project.common.enums.StatusLaporan
import org.example.project.ui.components.TopNavigationBar
import org.example.project.ui.components.BottomNavigationBar
import org.example.project.ui.viewmodel.ReportViewModel
import org.example.project.ui.components.UserFilterTabs
import org.example.project.ui.components.UserReportCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserDashboardScreen(
    navController: NavHostController,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val reports by viewModel.reports.collectAsState()
    val formattedReports = reports.filter { it.statusLaporan != StatusLaporan.DELETED }
    val selectedFilter = remember { mutableStateOf("Laporan") }

    LaunchedEffect(Unit) {
        viewModel.getReports()
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            TopNavigationBar()
            HeaderUserSection()
            Spacer(modifier = Modifier.height(8.dp))
            UserFilterTabs(
                selectedFilter = selectedFilter,
                modifier = Modifier.fillMaxWidth(),
            )

            when (selectedFilter.value) {
                "Laporan" -> {
                    LazyColumn {
                        items(formattedReports) { report ->
                            UserReportCard(
                                navController = navController,
                                report = report,
                                viewModel = viewModel
                            )
                        }
                    }
                }
                "Undangan" -> {
                    // Placeholder untuk daftar undangan
                    Text(
                        text = "Daftar undangan belum tersedia",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderUserSection() {
    Column(Modifier.padding(24.dp, 0.dp, 24.dp, 12.dp)) {
        Text(
            text = "Setiap suara memiliki kekuatan. Jangan takut untuk berbicara dan mencari bantuan.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Justify
        )
    }
}
