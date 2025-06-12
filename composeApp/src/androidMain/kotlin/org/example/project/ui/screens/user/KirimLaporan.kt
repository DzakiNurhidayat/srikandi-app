package org.example.project.ui.screens.user

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Brush
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlinx.coroutines.launch
import org.example.project.R
import org.example.project.common.enums.JenisKekerasan
import org.example.project.common.enums.StatusLaporan
import org.example.project.model.request.ReportRequest
import org.example.project.ui.viewmodel.ReportViewModel
import org.example.project.utils.toReadableString
import org.example.project.ui.components.BottomNavigationBar
import org.example.project.ui.components.DatePickerTextField
import org.example.project.ui.theme.altSurfaceVariant
import org.example.project.ui.theme.surfaceVariant
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TambahLaporanScreen(
    navController: NavHostController,
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uploadedFiles = remember { mutableStateListOf<Uri>() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris?.let { uploadedFiles.addAll(it) }
    }

    var isKorban by remember { mutableStateOf(true) }
    var deskripsi by remember { mutableStateOf("") }
    var selectedJenisKekerasan by remember { mutableStateOf(JenisKekerasan.entries.first()) }
    val jenisList = JenisKekerasan.entries
    var tempatKejadian by remember { mutableStateOf("") }
    var waktuKejadian by remember { mutableStateOf("") }
    var waktuKejadianDisplay by remember { mutableStateOf("") }
    var setujuWawancara by remember { mutableStateOf(false) }

    fun handleSubmit() {
        if (!setujuWawancara) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Anda harus menyetujui wawancara")
            }
            return
        }

        if (
            selectedJenisKekerasan == null ||
            deskripsi.isBlank() ||
            tempatKejadian.isBlank() ||
            waktuKejadian.isBlank() ||
            uploadedFiles.isEmpty()
        ) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Harap isi semua data dan unggah minimal satu bukti")
            }
            return
        }

        coroutineScope.launch {
            try {
                isLoading = true

                // Upload files first and get paths
                val evidencePathsResult = reportViewModel.uploadFilesAndGetPaths(context, uploadedFiles)
                val evidencePaths = evidencePathsResult.getOrThrow()

                // Create report request
                val reportRequest = ReportRequest(
                    isKorban = isKorban,
                    deskripsi = deskripsi,
                    jenisKekerasan = selectedJenisKekerasan!!,
                    tempatKejadian = tempatKejadian,
                    tanggalKejadian = waktuKejadian,
                    statusLaporan = StatusLaporan.DRAFT,
                    bukti = evidencePaths,
                )

                // Submit report
                reportViewModel.createReport(reportRequest).getOrThrow()

                // Show success message and navigate back
                snackbarHostState.showSnackbar("Laporan berhasil dikirim")
                navController.popBackStack()
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Gagal mengirim laporan: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ) {
                    Text(
                        text = data.visuals.message,
                        color = Color.White
                    )
                }
            }
        },
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize()
                .padding(padding)
                .clip(RoundedCornerShape(topEnd = 16.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Image(
                    painter = painterResource(id = R.drawable.srikandi),
                    contentDescription = "Logo Srikandi",
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(align = Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Pelaporan Kekerasan",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(align = Alignment.CenterHorizontally),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(36.dp))

                Text("Melapor Sebagai", color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isKorban,
                        onClick = { isKorban = true },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = surfaceVariant,
                            unselectedColor = Color.Gray
                        )
                    )
                    Text("Korban", color = Color.Black)
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(
                        selected = !isKorban,
                        onClick = { isKorban = false },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = surfaceVariant,
                            unselectedColor = Color.Gray
                        )
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
                    items = jenisList,
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = tempatKejadian,
                    onValueChange = { tempatKejadian = it },
                    label = { Text("Lokasi", color = Color.Black) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray)
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
                Text("Upload Bukti Pendukung", color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
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
                                onPreviewClick = { /* Akan ditangani di FilePreview */ }
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
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = setujuWawancara,
                        onCheckedChange = { setujuWawancara = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = surfaceVariant,
                            uncheckedColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Dengan melaporkan kasus, Anda setuju untuk dipanggil oleh tim Satgas guna wawancara lebih lanjut sesuai ketentuan Permendikbud No. 55 Tahun 2024.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
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
                        Text("Kirim Laporan")
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    selected: JenisKekerasan,
    onItemSelected: (JenisKekerasan) -> Unit,
    items: List<JenisKekerasan>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selected.toReadableString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Pilih Jenis Kekerasan", color = Color.Black) },
            trailingIcon = {
                Icon(
                    painter = painterResource(
                        id = if (expanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
                    ),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.toReadableString()) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun FilePreview(
    uri: Uri,
    onRemove: () -> Unit,
    onPreviewClick: () -> Unit
) {
    val context = LocalContext.current
    val type = remember(uri) { context.contentResolver.getType(uri) ?: "unknown" }
    var showPreviewDialog by remember { mutableStateOf(false) }

    val videoThumbnail by produceState<Bitmap?>(initialValue = null, uri) {
        if (type.startsWith("video")) {
            value = getVideoThumbnail(context, uri)
        }
    }

    val isAudio = type.startsWith("audio")

    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isAudio) Color.White else Color.LightGray)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .clickable { showPreviewDialog = true },
        contentAlignment = Alignment.Center
    ) {
        when {
            type.startsWith("image") -> {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Image Preview",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            type.startsWith("video") -> {
                videoThumbnail?.let { thumbnail ->
                    Image(
                        bitmap = thumbnail.asImageBitmap(),
                        contentDescription = "Video Preview",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray)
                    )
                }
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_play),
                    contentDescription = "Play",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    tint = Color.Black
                )
            }
            type.startsWith("audio") -> {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_ff),
                    contentDescription = "Audio",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
            else -> {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_info_details),
                    contentDescription = "Unknown",
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
                .clickable { onRemove() },
            tint = Color.Black
        )
    }

    if (showPreviewDialog) {
        MediaPreviewDialog(
            uri = uri,
            type = type,
            onDismiss = { showPreviewDialog = false }
        )
    }
}


// Fungsi untuk mendapatkan thumbnail video
fun getVideoThumbnail(context: Context, uri: Uri): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val bitmap = retriever.getFrameAtTime(0)
        retriever.release()
        bitmap
    } catch (e: Exception) {
        null
    }
}

@Composable
fun MediaPreviewDialog(
    uri: Uri,
    type: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    // Inisialisasi ExoPlayer untuk video/audio
    LaunchedEffect(uri) {
        if (type.startsWith("video") || type.startsWith("audio")) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(uri))
                prepare()
            }
        }
    }

    // Bersihkan ExoPlayer saat dialog ditutup
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    type.startsWith("image") -> {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Image Preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                    type.startsWith("video") -> {
                        exoPlayer?.let { player ->
                            AndroidView(
                                factory = { ctx ->
                                    PlayerView(ctx).apply {
                                        this.player = player
                                        useController = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                    type.startsWith("audio") -> {
                        exoPlayer?.let { player ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (isPlaying) {
                                            player.pause()
                                        } else {
                                            player.play()
                                        }
                                        isPlaying = !isPlaying
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            if (isPlaying) android.R.drawable.ic_media_pause
                                            else android.R.drawable.ic_media_play
                                        ),
                                        contentDescription = "Play/Pause"
                                    )
                                }
                                Text(
                                    text = "Audio Preview",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = "Unsupported file type",
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}