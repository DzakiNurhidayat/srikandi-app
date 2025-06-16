package org.example.project.ui.screens.user

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange // Pastikan ini diimport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // Tambahkan ini
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope // Mungkin tidak lagi diperlukan langsung di card
import androidx.media3.common.util.Log
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.project.R
import org.example.project.common.enums.StatusLaporan
import org.example.project.data.model.Filter
import org.example.project.model.entities.Report
import org.example.project.ui.components.FilterChip
import org.example.project.ui.screens.ketua.TotalCase
import org.example.project.ui.viewmodel.ReportViewModel
import org.example.project.utils.shadow
import java.text.SimpleDateFormat // Tambahkan ini
import java.util.Calendar // Tambahkan ini
import java.util.Locale // Tambahkan ini
import java.time.format.DateTimeFormatter // Jika masih menggunakan ini untuk format default
import androidx.compose.runtime.rememberCoroutineScope // <--- Tambahkan impor ini!
import kotlinx.coroutines.launch // <--- Tambahkan impor ini!
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import org.example.project.ui.theme.surfaceVariant

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserDashboardScreen(
    navController: NavHostController,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val reports by viewModel.reports.collectAsState()
    val formattedReports = reports.filter { it.statusLaporan != StatusLaporan.DELETED }
    val verifiedReports = reports.filter { it.statusLaporan == StatusLaporan.VERIFIED } // Untuk undangan

    val selectedFilter = remember { mutableStateOf("Laporan") }

    val PrimaryBlue = Color(0xFF3F51B5)
    val DarkGray = Color(0xFF333333)
    val MediumGray = Color(0xFF666666)
    val LightGrayBackground = Color(0xFFF5F5F5)

    LaunchedEffect(Unit) {
        viewModel.getReports()
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = LightGrayBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HeaderUserSection(darkGrayColor = DarkGray, mediumGrayColor = MediumGray)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                thickness = 2.dp,
                color = MediumGray.copy(alpha = 0.2f)
            )
            TotalCase(formattedReports.size, "Laporan Terkirim")
            UserFilterTabs(
                selectedFilter = selectedFilter,
                modifier = Modifier.fillMaxWidth(),
                primaryColor = PrimaryBlue,
                darkGrayColor = DarkGray,
                mediumGrayColor = MediumGray
            )

            when (selectedFilter.value) {
                "Laporan" -> {
                    if (formattedReports.isEmpty()) {
                        Text(
                            text = "Belum ada laporan terkirim.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkGray,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn {
                            items(formattedReports) { report ->
                                UserReportCard(
                                    navController = navController,
                                    report = report,
                                    viewModel = viewModel,
                                    primaryBlue = PrimaryBlue,
                                    darkGray = DarkGray,
                                    mediumGray = MediumGray,
                                    showCalendarIcon = false // TIDAK tampilkan ikon kalender untuk tab "Laporan"
                                )
                            }
                        }
                    }
                }
                "Undangan" -> {
                    if (verifiedReports.isEmpty()) {
                        Text(
                            text = "Belum ada undangan klarifikasi.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkGray,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn {
                            items(verifiedReports) { report ->
                                UserReportCard(
                                    navController = navController,
                                    report = report,
                                    viewModel = viewModel,
                                    primaryBlue = PrimaryBlue,
                                    darkGray = DarkGray,
                                    mediumGray = MediumGray,
                                    showCalendarIcon = true // TAMPILKAN ikon kalender untuk tab "Undangan"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// HeaderUserSection dan UserFilterTabs (tidak berubah, sesuaikan import)
@Composable
fun HeaderUserSection(darkGrayColor: Color, mediumGrayColor: Color) {
    Column(Modifier.padding(24.dp, 32.dp, 24.dp, 12.dp)) {
        Text(text = "Selamat Pagi,", style = MaterialTheme.typography.bodyMedium.copy(color = mediumGrayColor))
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Restu Akbar",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = darkGrayColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Setiap suara memiliki kekuatan. Jangan takut untuk berbicara dan mencari bantuan.",
            fontSize = 12.sp,
            color = mediumGrayColor,
            style = LocalTextStyle.current.copy(textAlign = TextAlign.Justify)
        )
    }
}

@Composable
fun UserFilterTabs(
    selectedFilter: MutableState<String>,
    modifier: Modifier = Modifier,
    chipWidth: Dp = 120.dp,
    chipHeight: Dp = 32.dp,
    primaryColor: Color,
    darkGrayColor: Color,
    mediumGrayColor: Color
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
                    enabled = mutableStateOf(isSelected)
                ),
                onSelected = {
                    selectedFilter.value = filter
                },
                modifier = Modifier
                    .width(chipWidth)
                    .height(chipHeight)
                    .padding(end = 5.dp),
                shape = RoundedCornerShape(20.dp),
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserReportCard(
    navController: NavHostController,
    report: Report,
    viewModel: ReportViewModel = hiltViewModel(),
    primaryBlue: Color,
    darkGray: Color,
    mediumGray: Color,
    showCalendarIcon: Boolean = false
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // State untuk tanggal klarifikasi yang akan ditampilkan atau dipilih
    // Inisialisasi dengan report.tanggalKlarifikasi jika sudah ada
    var selectedKlarifikasiDate by remember { mutableStateOf(report.tanggalPemanggilan ?: "") }

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    // DatePickerDialog definition
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val newDate = dateFormat.format(calendar.time)
                selectedKlarifikasiDate = newDate // Update state UI

                // Panggil fungsi BARU di ViewModel untuk mengupdate tanggal klarifikasi
                report.id?.let { id ->
                    viewModel.updateKlarifikasiDateForReport(id, newDate, report) // Passing report object
                }
            },
            // Set tanggal awal DatePicker ke tanggal yang sudah ada di report.tanggalKlarifikasi
            // Jika belum ada, gunakan tanggal hari ini
            (if (selectedKlarifikasiDate.toString().isNotBlank()) {
                try {
                    val parsedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedKlarifikasiDate.toString())
                    parsedDate?.let { calendar.time = it }
                    calendar.get(Calendar.YEAR)
                } catch (e: Exception) {
                    Calendar.getInstance().get(Calendar.YEAR)
                }
            } else {
                Calendar.getInstance().get(Calendar.YEAR)
            }),
            (if (selectedKlarifikasiDate.toString().isNotBlank()) {
                try {
                    val parsedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedKlarifikasiDate.toString())
                    parsedDate?.let { calendar.time = it }
                    calendar.get(Calendar.MONTH)
                } catch (e: Exception) {
                    Calendar.getInstance().get(Calendar.MONTH)
                }
            } else {
                Calendar.getInstance().get(Calendar.MONTH)
            }),
            (if (selectedKlarifikasiDate.toString().isNotBlank()) {
                try {
                    val parsedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedKlarifikasiDate.toString())
                    parsedDate?.let { calendar.time = it }
                    calendar.get(Calendar.DAY_OF_MONTH)
                } catch (e: Exception) {
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                }
            } else {
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            })
        ).apply {
            // Opsional: Set minimum date jika klarifikasi tidak bisa di masa lalu
            datePicker.minDate = System.currentTimeMillis() - 1000 // Tidak boleh kurang dari hari ini
        }
    }


    val formattedDate = remember(report.tanggalKejadian) {
        try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(report.tanggalPemanggilan.toString())
            val outputFormat = SimpleDateFormat("dd MMM𝖒𝖚", Locale("id"))
            outputFormat.format(date)
        } catch (e: Exception) {
            report.tanggalKejadian // Fallback jika parsing gagal
        }
    }

    val formattedKlarifikasiDate = remember(selectedKlarifikasiDate) {
        if (selectedKlarifikasiDate.toString().isNotBlank()) {
            try {
                val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = inputFormat.parse(selectedKlarifikasiDate.toString())
                val outputFormat = SimpleDateFormat("dd MMM𝖒𝖚", Locale("id"))
                outputFormat.format(date)
            } catch (e: Exception) {
                selectedKlarifikasiDate // Fallback jika parsing gagal
            }
        } else {
            "Pilih Tanggal Klarifikasi" // Teks default jika belum dipilih
        }
    }

    val coroutineScope = rememberCoroutineScope() // <--- DEFINISIKAN INI DI SINI!

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Hapus",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = darkGray
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus laporan ini?",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify,
                    color = mediumGray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Memanggil updateReport untuk mengubah status menjadi DELETED
                        report.id?.let { id ->
                            // GUNAKAN 'coroutineScope' YANG SUDAH DI-REMEMBER
                            coroutineScope.launch { // <-- PERBAIKAN PENTING DI SINI
                                try {
                                    viewModel.updateReport(id, StatusLaporan.DELETED)
                                    showDeleteDialog = false
                                } catch (e: Exception) {
                                    Log.e("UserReportCard", "Error deleting report: ${e.message}")
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
                        color = primaryBlue
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
                color = Color.Black.copy(alpha = 0.1f),
                borderRadius = 16.dp,
                blurRadius = 10.dp,
                offsetX = 0.dp,
                offsetY = 4.dp,
                spread = 0.dp
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    // Navigasi ke detail/edit laporan
                    navController.navigate("edit_laporan/${report.id}")
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.satgas_ppkpt),
                        contentDescription = "Laporan Icon",
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, RoundedCornerShape(percent = 50))
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Restu Akbar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = darkGray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Teknik Komputer dan Informatika ‘23",
                            color = mediumGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Setiap suara memiliki kekuatan. Jangan takut untuk berbicara dan mencari bantuan.",
                            fontSize = 12.sp,
                            // Perubahan di sini: Pindahkan textAlign ke dalam style
                            style = LocalTextStyle.current.copy(textAlign = TextAlign.Justify) // atau MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Justify)
                            // Jika Anda sudah punya style lain yang diterapkan, gunakan .copy() pada style tersebut
                        )
                    }
                }
                // Ikon Kalender dan Tanggal Klarifikasi
                if (showCalendarIcon) {
                    Column(horizontalAlignment = Alignment.End) {
                        IconButton(
                            onClick = { datePickerDialog.show() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Pilih Tanggal Klarifikasi",
                                tint = primaryBlue,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        // The Text composable here is correct with the String 'formattedKlarifikasiDate'
                        Text(
                            text = formattedKlarifikasiDate.toString(),
                            style = TextStyle(
                                color = darkGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                color = mediumGray.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = report.deskripsi,
                color = darkGray,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Tombol "Lihat Detail" untuk laporan VERIFIED
            if (report.statusLaporan == StatusLaporan.VERIFIED) {
                Button(
                    onClick = {
                        // TODO: Navigasi ke detail laporan yang diverifikasi
                        navController.navigate("detail_verifikasi/${report.id}")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Lihat Detail",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else { // Tombol Edit dan Hapus untuk status lainnya
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            navController.navigate("edit_laporan/${report.id}")
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, primaryBlue),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryBlue),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Edit Laporan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.Red),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Hapus Laporan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

