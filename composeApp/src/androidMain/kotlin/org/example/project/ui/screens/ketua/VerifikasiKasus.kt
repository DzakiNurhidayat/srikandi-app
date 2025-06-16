package org.example.project.ui.screens.ketua

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.project.common.enums.StatusLaporan
import org.example.project.ui.components.CustomButton
import org.example.project.ui.components.confirmationDialog
import org.example.project.ui.navigation.Screen
import org.example.project.ui.viewmodel.ReportViewModel
import org.example.project.ui.viewmodel.VerifikasiViewModel
import org.example.project.utils.toReadableString
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VerifikasiScreen(
    navController: NavHostController,
    reportviewModel: ReportViewModel,
    verifikasiViewModel: VerifikasiViewModel
) {
    val textSize = 16.sp
    val report by verifikasiViewModel.report
    var showRejectDialog by remember { mutableStateOf(false) }
    var showAcceptDialog by remember { mutableStateOf(false) }

    if (report != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Informasi Kejadian",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .border(BorderStroke(1.5.dp, color = Color.Black), shape = CircleShape)
                            .clickable { navController.popBackStack() }
                            .hoverable(remember { MutableInteractionSource() }),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
                HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.secondary)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 20.dp)
            ) {
                val infoList = listOf(
                    "Pelapor" to "Haikal Hariyanto",
                    "NIM/NIP" to "231511081",
                    "No Telp" to "095555555",
                    "Jurusan" to "Teknik Komputer dan Informatika '23",
                    "Status Pelapor" to if (report!!.isKorban) "Korban" else "Saksi",
                    "Tempat Kejadian" to (report!!.tempatKejadian ?: "-"),
                    "Waktu Kejadian" to "${report!!.tanggalKejadian} (${
                        ChronoUnit.DAYS.between(
                            LocalDate.parse(report!!.tanggalKejadian.toString()),
                            LocalDate.now()
                        )
                    } hari yang lalu)",
                    "Bentuk Kekerasan" to report!!.jenisKekerasan.toReadableString(),
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
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = value,
                                fontSize = textSize,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Text(
                    "Deskripsi Kejadian",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Light,
                    fontSize = textSize,
                    modifier = Modifier.padding(vertical = 15.dp)
                )
                TextField(
                    value = report?.deskripsi ?: "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .border(2.dp, MaterialTheme.colorScheme.secondary, shape = MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.background, shape = MaterialTheme.shapes.small),
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.White,
                        disabledTextColor = Color.Black
                    ),
                    enabled = false
                )

                Text(
                    "Bukti Pendukung",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Light,
                    fontSize = textSize,
                    modifier = Modifier.padding(vertical = 15.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.Gray)
                            .clickable { /* Handle image click */ }
                            .hoverable(remember { MutableInteractionSource() }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Image", color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.Gray)
                            .clickable { /* Handle video click */ }
                            .hoverable(remember { MutableInteractionSource() }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Video", color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.Gray)
                            .clickable { /* Handle mp3 click */ }
                            .hoverable(remember { MutableInteractionSource() }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("MP3", color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.secondary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CustomButton(
                    text = "Tolak",
                    onClick = { showRejectDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp),
                    contentColor = MaterialTheme.colorScheme.error
                )
                CustomButton(
                    text = "Terima",
                    onClick = { showAcceptDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Data tidak ditemukan")
        }
    }
    if (showRejectDialog) {
        confirmationDialog(
            title = "Tolak Laporan",
            message = "Apakah anda yakin ingin menolak laporan ini?",
            confirmText = "Tolak",
            confirmButtonColor = MaterialTheme.colorScheme.error,
            onConfirm = {
                showRejectDialog = false
                navController.currentBackStackEntry?.let { entry ->
                    entry.lifecycleScope.launch {
                        report?.id?.let {
                            reportviewModel.updateReport(it, StatusLaporan.REJECTED)
                        }
                        navController.navigate(Screen.DashboardKetua.route) {
                            popUpTo(Screen.DashboardKetua.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            },
            onDismiss = { showRejectDialog = false },
            dismissText = "Batal",
            icon = Icons.Default.Close
        )
    }
    if (showAcceptDialog) {
        confirmationDialog(
            title = "Tolak Laporan",
            message = "Apakah anda yakin ingin menerima laporan ini?",
            confirmText = "Terima",
            confirmButtonColor = MaterialTheme.colorScheme.primary,
            onConfirm = {
                showRejectDialog = false
                navController.currentBackStackEntry?.let { entry ->
                    entry.lifecycleScope.launch {
                        report?.id?.let {
                            reportviewModel.updateReport(it, StatusLaporan.VERIFIED)
                        }
                        navController.navigate(Screen.DashboardKetua.route) {
                            popUpTo(Screen.DashboardKetua.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            },
            onDismiss = { showRejectDialog = false },
            dismissText = "Batal",
            icon = Icons.Default.Done
        )
    }
}