package org.example.project.ui.screens.user

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import org.example.project.R
import org.example.project.common.enums.JenisKekerasan
import org.example.project.common.enums.StatusLaporan
import org.example.project.model.entities.Report
import org.example.project.model.request.ReportRequest
import org.example.project.ui.components.DatePickerTextField
import org.example.project.ui.components.TopNavigationBarB
import org.example.project.ui.viewmodel.ReportViewModel

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
    val baseUrl = remember { "http://192.168.1.2:8080/" }

    fun getEvidenceUrl(filePath: String, reportId: Int): String {
        // Log input filePath dan reportId
        Log.d("EvidenceUrl", "Input filePath: $filePath, reportId: $reportId")

        return if (filePath.startsWith("http") || filePath.startsWith("https://")) {
            Log.d("EvidenceUrl", "FilePath is already an HTTP URL: $filePath")
            filePath
        } else {
            Log.d("EvidenceUrl", "Final evidence URL: $filePath")
            baseUrl + filePath
        }
    }

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { padding ->
        if (isInitialLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
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
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                TopNavigationBarB(
                    text = "Detail Kejadian",
                    navController = navController,
                    onDeleteClick = {
                        reportId?.let { reportViewModel.deleteReport(it) }
                    }
                )
                HorizontalDivider(
                    color = Color.Gray.copy(alpha = 0.3f),
                    thickness = 2.dp,
                )

                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.srikandi),
                    contentDescription = "Logo Srikandi",
                    modifier = Modifier
                        .height(120.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Edit Laporan",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(36.dp))

                Text("Melapor Sebagai", color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isKorban,
                        onClick = { isKorban = true }
                    )
                    Text("Korban", color = Color.Black)
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(
                        selected = !isKorban,
                        onClick = { isKorban = false }
                    )
                    Text("Saksi", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi Kejadian", color = Color.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5,
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
                DropdownMenuBox(
                    selected = selectedJenisKekerasan,
                    onItemSelected = { selectedJenisKekerasan = it },
                    items = jenisList
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = tempatKejadian,
                    onValueChange = { tempatKejadian = it },
                    label = { Text("Lokasi", color = Color.Black) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.Black)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
                DatePickerTextField(
                    onDateSelected = { date ->
                        waktuKejadian = date // Format YYYY-MM-DD untuk pengiriman
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Bukti Pendukung", color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))

                // Existing evidence
                if (!report?.bukti.isNullOrEmpty()) {
                    Text(
                        "Bukti yang ada:",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 150.dp)
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(report?.bukti ?: emptyList()) { filePath ->
                                val uri = Uri.parse(getEvidenceUrl(filePath, reportId))
                                Log.d("FileEditPreview", "Loading URI: $uri")
                                val type = when {
                                    filePath.endsWith(".jpg", ignoreCase = true) || 
                                    filePath.endsWith(".jpeg", ignoreCase = true) || 
                                    filePath.endsWith(".png", ignoreCase = true) -> "image"
                                    filePath.endsWith(".mp4", ignoreCase = true) || 
                                    filePath.endsWith(".mov", ignoreCase = true) || 
                                    filePath.endsWith(".avi", ignoreCase = true) -> "video"
                                    filePath.endsWith(".mp3", ignoreCase = true) || 
                                    filePath.endsWith(".wav", ignoreCase = true) || 
                                    filePath.endsWith(".ogg", ignoreCase = true) -> "audio"
                                    else -> "unknown"
                                }
                                FileEditPreview(
                                    EvidenceUrl = filePath,
                                    uri = uri,
                                    onRemove = { /* Tidak ada hapus untuk bukti yang sudah ada */ },
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Tambah bukti baru:",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uploadedFiles) { uri ->
                            FilePreview(
                                uri = uri,
                                onRemove = { uploadedFiles.remove(uri) },
                                onPreviewClick = { /* Ditangani di FilePreview */ }
                            )
                        }

                        item {
                            IconButton(
                                onClick = {
                                    launcher.launch(arrayOf("image/*", "video/*", "audio/*"))
                                },
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                            ) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_upload),
                                    contentDescription = "Upload",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { handleSubmit() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F51B5),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray
                    )
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
    EvidenceUrl: String,
    uri: Uri,
    onRemove: () -> Unit
) {
    val ktorStaticBaseUrl = "http://192.168.1.2:8080"
    val context = LocalContext.current

    // Log the input parameters
    Log.d("FileEditPreview", "Processing evidence - URL: $EvidenceUrl, URI: $uri")

    // Deteksi tipe file untuk URL atau URI
    val type = remember(EvidenceUrl, uri) {
        val detectedType = when {
            EvidenceUrl.isNotBlank() -> {
                val fullUrl = if (EvidenceUrl.startsWith("http://") || EvidenceUrl.startsWith("https://")) {
                    EvidenceUrl // Already a full URL
                } else {
                    "$ktorStaticBaseUrl/$EvidenceUrl" // Prepend base URL
                }
                val path = fullUrl.substringAfterLast("/", "")
                Log.d("FileEditPreview", "Detecting type from path: $path")
                when {
                    path.endsWith(".jpg", true) || path.endsWith(".jpeg", true) || path.endsWith(".png", true) -> {
                        val mimeType = "image/${path.substringAfterLast('.', "jpeg").lowercase()}"
                        Log.d("FileEditPreview", "Detected image type: $mimeType")
                        mimeType
                    }
                    path.endsWith(".mp4", true) -> {
                        Log.d("FileEditPreview", "Detected video type: video/mp4")
                        "video/mp4"
                    }
                    path.endsWith(".mov", true) -> {
                        Log.d("FileEditPreview", "Detected video type: video/quicktime")
                        "video/quicktime"
                    }
                    path.endsWith(".avi", true) -> {
                        Log.d("FileEditPreview", "Detected video type: video/x-msvideo")
                        "video/x-msvideo"
                    }
                    path.endsWith(".mp3", true) -> {
                        Log.d("FileEditPreview", "Detected audio type: audio/mpeg")
                        "audio/mpeg"
                    }
                    path.endsWith(".wav", true) -> {
                        Log.d("FileEditPreview", "Detected audio type: audio/wav")
                        "audio/wav"
                    }
                    path.endsWith(".ogg", true) -> {
                        Log.d("FileEditPreview", "Detected audio type: audio/ogg")
                        "audio/ogg"
                    }
                    else -> {
                        val fallbackType = if (path.contains(".")) "image/${path.substringAfterLast('.').lowercase()}"
                        else "application/octet-stream"
                        Log.d("FileEditPreview", "Using fallback type: $fallbackType")
                        fallbackType
                    }
                }
            }
            uri != Uri.EMPTY -> {
                val mimeType = context.contentResolver.getType(uri)
                Log.d("FileEditPreview", "Detected MIME type from URI: $mimeType")
                mimeType ?: when {
                    uri.lastPathSegment?.endsWith(".jpg", true) == true ||
                            uri.lastPathSegment?.endsWith(".jpeg", true) == true ||
                            uri.lastPathSegment?.endsWith(".png", true) == true -> {
                        val imageType = "image/${uri.lastPathSegment?.substringAfterLast('.', "jpeg")?.lowercase()}"
                        Log.d("FileEditPreview", "Detected image type from URI: $imageType")
                        imageType
                    }
                    uri.lastPathSegment?.endsWith(".mp4", true) == true -> {
                        Log.d("FileEditPreview", "Detected video type from URI: video/mp4")
                        "video/mp4"
                    }
                    uri.lastPathSegment?.endsWith(".mov", true) == true -> {
                        Log.d("FileEditPreview", "Detected video type from URI: video/quicktime")
                        "video/quicktime"
                    }
                    uri.lastPathSegment?.endsWith(".avi", true) == true -> {
                        Log.d("FileEditPreview", "Detected video type from URI: video/x-msvideo")
                        "video/x-msvideo"
                    }
                    uri.lastPathSegment?.endsWith(".mp3", true) == true -> {
                        Log.d("FileEditPreview", "Detected audio type from URI: audio/mpeg")
                        "audio/mpeg"
                    }
                    uri.lastPathSegment?.endsWith(".wav", true) == true -> {
                        Log.d("FileEditPreview", "Detected audio type from URI: audio/wav")
                        "audio/wav"
                    }
                    uri.lastPathSegment?.endsWith(".ogg", true) == true -> {
                        Log.d("FileEditPreview", "Detected audio type from URI: audio/ogg")
                        "audio/ogg"
                    }
                    else -> {
                        Log.d("FileEditPreview", "Using fallback type for URI: application/octet-stream")
                        "application/octet-stream"
                    }
                }
            }
            else -> {
                Log.w("FileEditPreview", "No type detected - using unknown")
                "unknown"
            }
        }
        Log.d("FileEditPreview", "Final detected type: $detectedType")
        detectedType
    }

    var showPreviewDialog by remember { mutableStateOf(false) }
    val videoThumbnail by produceState<Bitmap?>(initialValue = null, uri, type) {
        if (type.startsWith("video")) {
            try {
                Log.d("FileEditPreview", "Attempting to get video thumbnail for URI: $uri")
                value = getVideoThumbnail(context, uri)
                if (value != null) {
                    Log.d("FileEditPreview", "Successfully loaded video thumbnail")
                } else {
                    Log.w("FileEditPreview", "Failed to load video thumbnail - returned null")
                }
            } catch (e: Exception) {
                Log.e("FileEditPreview", "Error getting video thumbnail: ${e.message}", e)
                value = null
            }
        }
    }
    val isAudio = type.startsWith("audio")

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(uri)
            .crossfade(true)
            .error(android.R.drawable.stat_notify_error)
            .fallback(android.R.drawable.ic_menu_gallery)
            .build(),
        onError = { error ->
            Log.e("FileEditPreview", "Image loading failed for URI $uri: ${error.result.throwable?.message}")
        }
    )

    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isAudio) Color.White else Color.LightGray)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .clickable { 
                Log.d("FileEditPreview", "Opening preview dialog for type: $type")
                showPreviewDialog = true 
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            type.startsWith("image") -> {
                when (painter.state) {
                    is AsyncImagePainter.State.Success -> {
                        Log.d("FileEditPreview", "Successfully loaded image")
                        Image(
                            painter = painter,
                            contentDescription = "Pratinjau Gambar",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    is AsyncImagePainter.State.Error -> {
                        Log.e("FileEditPreview", "Image loading error state: ${painter.state}")
                        Icon(
                            painter = painterResource(id = android.R.drawable.stat_notify_error),
                            contentDescription = "Gagal memuat gambar",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Red
                        )
                    }
                    else -> {
                        Log.d("FileEditPreview", "Loading image...")
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
            type.startsWith("video") -> {
                videoThumbnail?.let { thumbnail ->
                    Log.d("FileEditPreview", "Displaying video thumbnail")
                    Image(
                        bitmap = thumbnail.asImageBitmap(),
                        contentDescription = "Pratinjau Video",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } ?: run {
                    Log.w("FileEditPreview", "No video thumbnail available")
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.stat_notify_error),
                            contentDescription = "Gagal memuat thumbnail",
                            modifier = Modifier.size(24.dp).align(Alignment.Center),
                            tint = Color.Red
                        )
                    }
                }
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_play),
                    contentDescription = "Putar",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    tint = Color.White
                )
            }
            type.startsWith("audio") -> {
                Log.d("FileEditPreview", "Displaying audio icon")
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_ff),
                    contentDescription = "Audio",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
            else -> {
                Log.w("FileEditPreview", "Unsupported type: $type")
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_info_details),
                    contentDescription = "Tidak Diketahui",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
        }

        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = "Hapus",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .size(16.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                .clickable { 
                    Log.d("FileEditPreview", "Removing evidence")
                    onRemove() 
                },
            tint = Color.Black
        )
    }

    if (showPreviewDialog) {
        Log.d("FileEditPreview", "Showing media preview dialog")
        MediaPreviewDialog(
            uri = uri,
            type = type,
            onDismiss = { 
                Log.d("FileEditPreview", "Closing media preview dialog")
                showPreviewDialog = false 
            }
        )
    }
}