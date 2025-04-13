package org.example.project.ui.screens.user

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import org.example.project.common.enums.JenisKekerasan
import org.example.project.common.enums.StatusLaporan
import org.example.project.data.model.NavItem
import org.example.project.model.request.ReportRequest
import org.example.project.ui.navigation.Screen
import org.example.project.ui.viewmodel.ReportViewModel
import org.example.project.utils.toReadableString

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
    var selectedJenisKekerasan by remember { mutableStateOf(JenisKekerasan.KekerasanFisik) }
    val jenisList = JenisKekerasan.entries
    var tempatKejadian by remember { mutableStateOf("") }
    var waktuKejadian by remember { mutableStateOf("") }
    var setujuWawancara by remember { mutableStateOf(false) }

    fun handleSubmit() {
        if (!setujuWawancara) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Anda harus menyetujui wawancara")
            }
            return
        }

        if (deskripsi.isBlank() || tempatKejadian.isBlank() || waktuKejadian.isBlank()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Harap isi semua field yang diperlukan")
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
                    jenisKekerasan = selectedJenisKekerasan,
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { BottomNavigationBar(navController) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = android.R.drawable.ic_input_add),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(80.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Pelaporan Kekerasan",
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
            Text("Upload Bukti Pendukung")
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow {
                items(uploadedFiles) { uri ->
                    FilePreview(uri = uri, onRemove = { uploadedFiles.remove(uri) })
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

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = setujuWawancara,
                    onCheckedChange = { setujuWawancara = it }
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
                enabled = !isLoading
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
            label = { Text("Pilih Jenis Kekerasan") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
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

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val items = listOf(
        NavItem(
            route = Screen.DashboardUser.route,
            icon = Icons.Default.Home,
            label = "Home"
        ),
        NavItem(
            route = Screen.KirimLaporan.route,
            icon = Icons.Default.AddCircle,
            label = "Laporan"
        ),
        NavItem(
            route = Screen.DashboardKetua.route,
            icon = Icons.Default.Person,
            label = "Ketua"
        )
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}