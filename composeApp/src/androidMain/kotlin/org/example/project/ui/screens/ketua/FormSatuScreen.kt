package org.example.project.ui.screens.ketua

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.project.common.enums.JenisKekerasan
import org.example.project.common.enums.StatusLaporan
import org.example.project.common.enums.StatusTerlapor
import org.example.project.model.entities.FormSatu
import org.example.project.model.entities.Report
import org.example.project.model.request.FormSatuRequest
import org.example.project.ui.navigation.Screen
import org.example.project.ui.viewmodel.FormSatuViewModel
import org.example.project.ui.viewmodel.VerifikasiViewModel
import org.example.project.utils.toReadableString
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FormSatuScreen(
    navController: NavHostController,
    verifikasiViewModel: VerifikasiViewModel,
    formSatuViewModel: FormSatuViewModel = hiltViewModel()
) {
    val report = verifikasiViewModel.report.value
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Custom colors
    val primaryBlue = Color(0xFF004499)
    val lightBlue = Color(0xFF6699CC)

    // Poppins font family (fallback to default if not available)
    val poppinsFont = FontFamily.Default

    // State untuk menyimpan data form
    var domisili by remember { mutableStateOf("") }
    var ciriFisik by remember { mutableStateOf("") }
    var memilikiDisabilitas by remember { mutableStateOf(false) }
    var ceritaSingkat by remember { mutableStateOf("") }
    var kontakLain by remember { mutableStateOf("") }
    var kebutuhanKorban by remember { mutableStateOf("") }
    var statusTerlapor by remember { mutableStateOf(StatusTerlapor.Mahasiswa) }
    var jenisKelaminTerlapor by remember { mutableStateOf(false) }
    var alasanPengaduan by remember { mutableStateOf("") }
    var namaTerlapor by remember { mutableStateOf("") } // Kolom baru, diabaikan jika tidak diperlukan
    var isLoading by remember { mutableStateOf(false) }

    // Atur reportId di ViewModel dan load data FormSatu yang sudah ada
    LaunchedEffect(report) {
        report?.id?.let { reportId ->
            formSatuViewModel.setReportId(reportId)
            isLoading = true
            val result = formSatuViewModel.getFormSatu()
            isLoading = false
            result.onSuccess { formSatu ->
                domisili = formSatu.domisili
                ciriFisik = formSatu.ciriFisik
                memilikiDisabilitas = formSatu.memilikiDisabilitas
                ceritaSingkat = formSatu.ceritaSingkat
                kontakLain = formSatu.kontakLain
                kebutuhanKorban = formSatu.kebutuhanKorban
                statusTerlapor = formSatu.statusTerlapor
                jenisKelaminTerlapor = formSatu.jenisKelaminTerlapor
                alasanPengaduan = formSatu.alasanPengaduan
            }.onFailure { exception ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Gagal memuat data Form 1: ${exception.message}"
                    )
                }
            }
        }
    }

    MaterialTheme {
        if (report != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(scrollState)
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 30.dp, vertical = 20.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Formulir 1",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            fontFamily = poppinsFont,
                            color = primaryBlue
                        )
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .border(BorderStroke(1.5.dp, primaryBlue), CircleShape)
                                .clickable { navController.popBackStack() }
                                .hoverable(remember { MutableInteractionSource() }),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close",
                                tint = primaryBlue,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                    Divider(thickness = 2.dp, color = primaryBlue)

                    // Informasi Kejadian
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp, vertical = 20.dp)
                    ) {
                        val textSize = 16.sp
                        Text(
                            text = "Informasi Kejadian",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = poppinsFont,
                            color = primaryBlue,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        val infoList = listOf(
                            "Id Laporan" to (report.id.toString()),
                            "Pelapor" to "-",
                            "Bentuk Kekerasan" to (report.jenisKekerasan?.toReadableString() ?: "-"),
                            "Waktu Kejadian" to "${report.tanggalKejadian?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id")))} (${
                                report.tanggalKejadian?.let {
                                    ChronoUnit.DAYS.between(it, LocalDate.now())
                                } ?: "0"
                            } hari yang lalu)",
                            "Tempat Kejadian" to (report.tempatKejadian ?: "-"),
                            "Deskripsi Kejadian" to (report.deskripsi ?: "-")
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            infoList.forEach { (label, value) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Light,
                                        fontSize = textSize,
                                        fontFamily = poppinsFont,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = value,
                                        fontSize = textSize,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = poppinsFont,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))

                    // Informasi Korban
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp, vertical = 20.dp)
                    ) {
                        Text(
                            text = "Informasi Korban",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = poppinsFont,
                            color = primaryBlue,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        OutlinedTextField(
                            value = domisili,
                            onValueChange = {
                                domisili = it
                                formSatuViewModel.updateDomisili(it)
                            },
                            label = {
                                Text(
                                    "Domisili",
                                    fontFamily = poppinsFont,
                                    color = primaryBlue
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryBlue,
                                unfocusedBorderColor = lightBlue,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = primaryBlue,
                                unfocusedLabelColor = primaryBlue
                            )
                        )
                        OutlinedTextField(
                            value = ciriFisik,
                            onValueChange = {
                                ciriFisik = it
                                formSatuViewModel.updateCiriFisik(it)
                            },
                            label = {
                                Text(
                                    "Ciri Fisik",
                                    fontFamily = poppinsFont,
                                    color = primaryBlue
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryBlue,
                                unfocusedBorderColor = lightBlue,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = primaryBlue,
                                unfocusedLabelColor = primaryBlue
                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Memiliki Disabilitas:",
                                color = primaryBlue,
                                fontSize = 16.sp,
                                fontFamily = poppinsFont,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Row(
                                modifier = Modifier.clickable { memilikiDisabilitas = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = memilikiDisabilitas,
                                    onClick = {
                                        memilikiDisabilitas = true
                                        formSatuViewModel.updateMemilikiDisabilitas(true)
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = primaryBlue,
                                        unselectedColor = lightBlue
                                    )
                                )
                                Text(
                                    "Ya",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontFamily = poppinsFont
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Row(
                                modifier = Modifier.clickable { memilikiDisabilitas = false },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = !memilikiDisabilitas,
                                    onClick = {
                                        memilikiDisabilitas = false
                                        formSatuViewModel.updateMemilikiDisabilitas(false)
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = primaryBlue,
                                        unselectedColor = lightBlue
                                    )
                                )
                                Text(
                                    "Tidak",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontFamily = poppinsFont
                                )
                            }
                        }
                        OutlinedTextField(
                            value = ceritaSingkat,
                            onValueChange = {
                                ceritaSingkat = it
                                formSatuViewModel.updateCeritaSingkat(it)
                            },
                            label = {
                                Text(
                                    "Cerita Singkat",
                                    fontFamily = poppinsFont,
                                    color = primaryBlue
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp)
                                .padding(vertical = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryBlue,
                                unfocusedBorderColor = lightBlue,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = primaryBlue,
                                unfocusedLabelColor = primaryBlue
                            )
                        )
                        OutlinedTextField(
                            value = kontakLain,
                            onValueChange = {
                                kontakLain = it
                                formSatuViewModel.updateKontakLain(it)
                            },
                            label = {
                                Text(
                                    "Kontak Lain",
                                    fontFamily = poppinsFont,
                                    color = primaryBlue
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryBlue,
                                unfocusedBorderColor = lightBlue,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = primaryBlue,
                                unfocusedLabelColor = primaryBlue
                            )
                        )
                        OutlinedTextField(
                            value = kebutuhanKorban,
                            onValueChange = {
                                kebutuhanKorban = it
                                formSatuViewModel.updateKebutuhanKorban(it)
                            },
                            label = {
                                Text(
                                    "Kebutuhan Korban",
                                    fontFamily = poppinsFont,
                                    color = primaryBlue
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp)
                                .padding(vertical = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryBlue,
                                unfocusedBorderColor = lightBlue,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = primaryBlue,
                                unfocusedLabelColor = primaryBlue
                            )
                        )
                    }

                    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))

                    // Informasi Pelaku
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp, vertical = 20.dp)
                    ) {
                        Text(
                            text = "Informasi Pelaku",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = poppinsFont,
                            color = primaryBlue,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        // Nama Terlapor - Kolom baru (diabaikan jika tidak diperlukan)
                        OutlinedTextField(
                            value = namaTerlapor,
                            onValueChange = { namaTerlapor = it },
                            label = {
                                Text(
                                    "Nama Terlapor",
                                    fontFamily = poppinsFont,
                                    color = primaryBlue
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryBlue,
                                unfocusedBorderColor = lightBlue,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = primaryBlue,
                                unfocusedLabelColor = primaryBlue
                            )
                        )

                        var expandedStatusTerlapor by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = statusTerlapor.name,
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text(
                                        "Status Terlapor",
                                        fontFamily = poppinsFont,
                                        color = primaryBlue
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { expandedStatusTerlapor = !expandedStatusTerlapor }) {
                                        Icon(
                                            imageVector = if (expandedStatusTerlapor) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                            contentDescription = null,
                                            tint = primaryBlue
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedStatusTerlapor = true },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryBlue,
                                    unfocusedBorderColor = lightBlue,
                                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                    focusedLabelColor = primaryBlue,
                                    unfocusedLabelColor = primaryBlue
                                )
                            )
                            DropdownMenu(
                                expanded = expandedStatusTerlapor,
                                onDismissRequest = { expandedStatusTerlapor = false },
                                modifier = Modifier
                                    .width(200.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                            ) {
                                StatusTerlapor.entries.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                selectionOption.name,
                                                fontFamily = poppinsFont,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        },
                                        onClick = {
                                            statusTerlapor = selectionOption
                                            formSatuViewModel.updateStatusTerlapor(selectionOption)
                                            expandedStatusTerlapor = false
                                        },
                                        modifier = Modifier.background(Color.Transparent)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Jenis Kelamin Terlapor",
                            color = primaryBlue,
                            fontFamily = poppinsFont,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.clickable {
                                    jenisKelaminTerlapor = false
                                    formSatuViewModel.updateJenisKelaminTerlapor(false)
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = !jenisKelaminTerlapor,
                                    onClick = {
                                        jenisKelaminTerlapor = false
                                        formSatuViewModel.updateJenisKelaminTerlapor(false)
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = primaryBlue,
                                        unselectedColor = lightBlue
                                    )
                                )
                                Text(
                                    "Laki-Laki",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontFamily = poppinsFont
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Row(
                                modifier = Modifier.clickable {
                                    jenisKelaminTerlapor = true
                                    formSatuViewModel.updateJenisKelaminTerlapor(true)
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = jenisKelaminTerlapor,
                                    onClick = {
                                        jenisKelaminTerlapor = true
                                        formSatuViewModel.updateJenisKelaminTerlapor(true)
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = primaryBlue,
                                        unselectedColor = lightBlue
                                    )
                                )
                                Text(
                                    "Perempuan",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontFamily = poppinsFont
                                )
                            }
                        }
                        OutlinedTextField(
                            value = alasanPengaduan,
                            onValueChange = {
                                alasanPengaduan = it
                                formSatuViewModel.updateAlasanPengaduan(it)
                            },
                            label = {
                                Text(
                                    "Alasan Pengaduan",
                                    fontFamily = poppinsFont,
                                    color = primaryBlue
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp)
                                .padding(vertical = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryBlue,
                                unfocusedBorderColor = lightBlue,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = primaryBlue,
                                unfocusedLabelColor = primaryBlue
                            )
                        )
                    }

                    var isButtonPressed by remember { mutableStateOf(false) }
                    val interactionSource = remember { MutableInteractionSource() }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                // Update semua field di ViewModel
                                formSatuViewModel.updateDomisili(domisili)
                                formSatuViewModel.updateCiriFisik(ciriFisik)
                                formSatuViewModel.updateMemilikiDisabilitas(memilikiDisabilitas)
                                formSatuViewModel.updateCeritaSingkat(ceritaSingkat)
                                formSatuViewModel.updateKontakLain(kontakLain)
                                formSatuViewModel.updateKebutuhanKorban(kebutuhanKorban)
                                formSatuViewModel.updateStatusTerlapor(statusTerlapor)
                                formSatuViewModel.updateJenisKelaminTerlapor(jenisKelaminTerlapor)
                                formSatuViewModel.updateAlasanPengaduan(alasanPengaduan)

                                // Simpan FormSatu
                                val result = formSatuViewModel.createFormSatu()
                                isLoading = false
                                result.onSuccess {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Form 1 berhasil disimpan")
                                        navController.navigate(Screen.DashboardKetua.route) {
                                            popUpTo(Screen.DashboardKetua.route) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                }.onFailure { exception ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Gagal menyimpan Form 1: ${exception.message}"
                                        )
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp, vertical = 20.dp)
                            .height(56.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue,
                            contentColor = Color.White,
                            disabledContainerColor = lightBlue,
                            disabledContentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp,
                            hoveredElevation = 6.dp
                        ),
                        interactionSource = interactionSource
                    ) {
                        if (isLoading) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Menyimpan...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = poppinsFont,
                                    color = Color.White
                                )
                            }
                        } else {
                            Text(
                                text = "Simpan Form 1",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = poppinsFont,
                                color = Color.White
                            )
                        }
                    }
                }

                // Indikator loading
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = primaryBlue
                    )
                }

                // Pesan error
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) { snackbarData ->
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ) {
                        Text(
                            snackbarData.visuals.message,
                            fontFamily = poppinsFont
                        )
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Data laporan tidak ditemukan.",
                    fontFamily = poppinsFont,
                    color = primaryBlue
                )
            }
        }
    }
}

