package org.example.project.ui.screens.user

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import org.example.project.common.ServerConfig
import org.example.project.common.enums.JenisKekerasan
import org.example.project.common.enums.StatusLaporan
import org.example.project.model.entities.Report
import org.example.project.model.request.ReportRequest
import org.example.project.ui.viewmodel.ReportViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditLaporanScreen(
    navController: NavHostController,
    reportId: Int,
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var report by remember { mutableStateOf<Report?>(null) }
    val uploadedFiles = remember { mutableStateListOf<Uri>() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(false) }
    var isInitialLoading by remember { mutableStateOf(true) }

    // Load report data
    LaunchedEffect(reportId) {
        try {
            isInitialLoading = true
            report = reportViewModel.getReportById(reportId)
        } catch (e: Exception) {
            snackbarHostState.showSnackbar("Gagal memuat data laporan: ${e.message}")
        } finally {
            isInitialLoading = false
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris?.let { uploadedFiles.addAll(it) }
    }

    var isKorban by remember { mutableStateOf(report?.isKorban ?: true) }
    var deskripsi by remember { mutableStateOf(report?.deskripsi ?: "") }
    var selectedJenisKekerasan by remember { mutableStateOf(report?.jenisKekerasan ?: JenisKekerasan.KekerasanFisik) }
    val jenisList = JenisKekerasan.entries
    var tempatKejadian by remember { mutableStateOf(report?.tempatKejadian ?: "") }
    var waktuKejadian by remember { mutableStateOf(report?.tanggalKejadian?.toString() ?: "") }

    // Update form fields when report data changes
    LaunchedEffect(report) {
        report?.let {
            isKorban = it.isKorban
            deskripsi = it.deskripsi
            selectedJenisKekerasan = it.jenisKekerasan
            tempatKejadian = it.tempatKejadian
            waktuKejadian = it.tanggalKejadian?.toString() ?: ""
        }
    }

    fun handleSubmit() {
        if (deskripsi.isBlank() || tempatKejadian.isBlank() || waktuKejadian.isBlank()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Harap isi semua field yang diperlukan")
            }
            return
        }

        coroutineScope.launch {
            try {
                isLoading = true

                // Upload new files if any and get paths
                val newEvidencePaths = if (uploadedFiles.isNotEmpty()) {
                    reportViewModel.uploadFilesAndGetPaths(context, uploadedFiles).getOrThrow()
                } else {
                    emptyList()
                }

                // Combine existing and new evidence paths
                val allEvidencePaths = (report?.bukti ?: emptyList()) + newEvidencePaths

                // Create report request
                val reportRequest = ReportRequest(
                    isKorban = isKorban,
                    deskripsi = deskripsi,
                    jenisKekerasan = selectedJenisKekerasan,
                    tempatKejadian = tempatKejadian,
                    tanggalKejadian = waktuKejadian,
                    statusLaporan = report?.statusLaporan ?: StatusLaporan.DRAFT,
                    bukti = allEvidencePaths
                )

                // Update report
                reportViewModel.updateReport(reportId, reportRequest).getOrThrow()

                // Show success message and navigate back
                snackbarHostState.showSnackbar("Laporan berhasil diperbarui")
                navController.popBackStack()
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Gagal memperbarui laporan: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isInitialLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Edit Laporan",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(36.dp))

                Text("Melapor Sebagai")
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isKorban,
                        onClick = { isKorban = true }
                    )
                    Text("Korban")
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(
                        selected = !isKorban,
                        onClick = { isKorban = false }
                    )
                    Text("Saksi")
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi Kejadian") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5,
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))
                DropdownMenuBox(
                    selected = selectedJenisKekerasan,
                    onItemSelected = { selectedJenisKekerasan = it },
                    items = jenisList
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = tempatKejadian,
                        onValueChange = { tempatKejadian = it },
                        label = { Text("Lokasi") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = waktuKejadian,
                        onValueChange = { waktuKejadian = it},
                        label = { Text("Tanggal") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Bukti Pendukung")
                Spacer(modifier = Modifier.height(4.dp))
                
                // Existing evidence
                if (!report?.bukti.isNullOrEmpty()) {
                    Text(
                        "Bukti yang ada:",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    LazyRow {
                        items(report?.bukti ?: emptyList()) { filePath ->
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.LightGray)
                            ) {
                                val fileExtension = filePath.substringAfterLast('.', "").lowercase()
                                when {
                                    fileExtension in listOf("jpg", "jpeg", "png") -> {
                                        Image(
                                            // Sementara masih assign ip manual, nanti akan disesuaikan kembali
//                                            painter = rememberAsyncImagePainter("10.0.2.2/${filePath}"),
                                            painter = rememberAsyncImagePainter("http://192.168.1.9:8080/${filePath}"),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    fileExtension in listOf("mp4", "mov", "avi") -> {
                                        Icon(
                                            painter = painterResource(id = android.R.drawable.ic_media_play),
                                            contentDescription = "Video",
                                            tint = Color.White,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                    fileExtension in listOf("mp3", "wav", "ogg") -> {
                                        Icon(
                                            painter = painterResource(id = android.R.drawable.ic_media_ff),
                                            contentDescription = "Audio",
                                            tint = Color.White,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Tambah bukti baru:",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                LazyRow {
                    items(uploadedFiles) { uri ->
                        FileEditPreview(uri = uri, onRemove = { uploadedFiles.remove(uri) })
                    }

                    item {
                        IconButton(
                            onClick = {
                                launcher.launch(arrayOf("image/*", "video/*", "audio/*"))
                            },
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_upload),
                                contentDescription = "Upload"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { handleSubmit() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Simpan Perubahan")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FileEditPreview(
    uri: Uri,
    onRemove: () -> Unit
) {
    val context = LocalContext.current
    val type = remember(uri) {
        context.contentResolver.getType(uri) ?: "unknown"
    }

    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(70.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(20.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Hapus",
                modifier = Modifier.size(16.dp)
            )
        }

        when {
            type.startsWith("image") -> {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            type.startsWith("video") -> {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_play),
                    contentDescription = "Video",
                    modifier = Modifier.size(24.dp)
                )
            }
            type.startsWith("audio") -> {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_ff),
                    contentDescription = "Audio",
                    modifier = Modifier.size(24.dp)
                )
            }
            else -> {
                Text(
                    text = uri.lastPathSegment?.take(6) ?: "File",
                    fontSize = 10.sp
                )
            }
        }
    }
} 