package org.example.project.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.project.R
import org.example.project.common.enums.StatusLaporan
import org.example.project.model.entities.Report
import org.example.project.model.request.ReportRequest
import org.example.project.ui.viewmodel.ReportViewModel
import java.time.format.DateTimeFormatter
import org.example.project.utils.toReadableString
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserReportCard(
    navController: NavHostController,
    report: Report,
    viewModel: ReportViewModel = hiltViewModel()
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val formattedDate = remember(report.tanggalKejadian) {
        report.tanggalKejadian.format(DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale("id")))
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
                    textAlign = TextAlign.Justify
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            report.id?.let { id ->
                                val reportRequest = ReportRequest(
                                    isKorban = report.isKorban,
                                    deskripsi = report.deskripsi,
                                    jenisKekerasan = report.jenisKekerasan,
                                    tempatKejadian = report.tempatKejadian,
                                    tanggalKejadian = report.tanggalKejadian.toString(),
                                    statusLaporan = StatusLaporan.DELETED,
                                    bukti = report.bukti
                                )
                                viewModel.editReport(id, reportRequest)
                            }
                        }
                        showDeleteDialog = false
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

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
//        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable {
                    report.id?.let { navController.navigate("edit_laporan/$it") }
                }
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = report.jenisKekerasan.toReadableString(),
                    fontSize = 12.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Main content
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.laporan),
                    contentDescription = "Laporan",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(8.dp)
                )
                Column {
                    Text(
                        text = "Laporan KS/2025/001",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF999999)
                    )
                    Text(
                        text = "Detail Laporan ...",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = report.deskripsi,
                color = Color.Black,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_clock),
                        contentDescription = "Tanggal",
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = formattedDate,
                        color = Color(0xFF666666),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = "Lokasi",
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = report.tempatKejadian,
                        color = Color(0xFF666666),
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            when (report.statusLaporan) {
                StatusLaporan.DRAFT -> {
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.Red),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Hapus",
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Hapus Laporan",
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                StatusLaporan.DELETED -> {
                    // Tidak menampilkan tombol untuk status DELETED
                }
                else -> {
                    Button(
                        onClick = {
                            report.id?.let { navController.navigate("edit_laporan/$it") }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E88E5),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Detail Laporan",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}