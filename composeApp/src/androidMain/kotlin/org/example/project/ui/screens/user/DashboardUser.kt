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
<<<<<<< HEAD
=======

@Composable
fun UserFilterTabs(
    selectedFilter: MutableState<String>,
    modifier: Modifier = Modifier,
    chipWidth: Dp = 120.dp,
    chipHeight: Dp = 32.dp
) {
    val filters = listOf("Laporan", "Undangan")
    LazyRow(
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(filters) { filter ->
            val isSelected = selectedFilter.value == filter
            FilterChip(
                filter = Filter(
                    name = filter,
                    enabled = remember { mutableStateOf(isSelected) }
                ),
                onSelected = {
                    selectedFilter.value = filter
                },
                modifier = Modifier
                    .width(chipWidth)
                    .height(chipHeight)
                    .padding(end = 5.dp),
                shape = RoundedCornerShape(20),
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserReportCard(
    navController: NavHostController,
    report: Report,
    viewModel: ReportViewModel = hiltViewModel()
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val formattedDate = remember(report.tanggalKejadian) {
        report.tanggalKejadian.format(DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale("id")))
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Hapus",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus laporan ini?",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        navController.currentBackStackEntry?.let { entry ->
                            entry.lifecycleScope.launch {
                                try {
                                    report.id?.let {
                                        viewModel.updateReport(it, StatusLaporan.DELETED)
                                    }
                                    showDeleteDialog = false
                                } catch (e: Exception) {
                                    showDeleteDialog = false
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        "Hapus",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(
                        "Batal",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .shadow(
                color = Color.Black.copy(alpha = 0.2f),
                borderRadius = 20.dp,
                blurRadius = 15.dp,
                offsetX = 2.dp,
                offsetY = 8.dp,
                spread = 0.dp
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    navController.navigate("edit_laporan/${report.id}")
                }
        ) {
            // Row: Image and Text (Name + Department/Date)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.satgas_ppkpt),
                    contentDescription = "Laporan",
                    modifier = Modifier
                        .height(60.dp)
                        .width(60.dp)
                )
                Column {
                    // Row 1: Name
                    Text(
                        text = "Restu Akbar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    // Row 2: Department and Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Teknik Komputer dan Informatika ‘23",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = formattedDate,
                            color = Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Divider
            HorizontalDivider(
                color = Color.Gray.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Description
            Text(
                text = report.deskripsi,
                color = Color.Black,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        navController.navigate("edit_laporan/${report.id}")
                    },
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Edit Laporan",
                        color = Color(0xFF666666),
                        fontSize = 14.sp
                    )
                }
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Red),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Hapus Laporan",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
>>>>>>> 5eee71efa9c91387bdfa073c465ba99b1f2de2c6
