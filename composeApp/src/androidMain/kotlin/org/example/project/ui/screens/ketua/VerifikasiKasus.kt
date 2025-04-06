package org.example.project.ui.screens.ketua

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.example.project.common.enums.StatusLaporan
import org.example.project.ui.components.CustomButton
import org.example.project.ui.theme.Divider
import org.example.project.ui.theme.TextVerifikasi
import org.example.project.ui.viewmodel.ReportViewModel
import org.example.project.ui.viewmodel.shared.SharedReportViewModel
import org.example.project.utils.toReadableString
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VerifikasiScreen(
    navController: NavHostController,
    viewModel: ReportViewModel = hiltViewModel(),
    sharedViewModel: SharedReportViewModel
) {
    val textSize = 16.sp
    val report = sharedViewModel.selectedReport.value
    Log.d("VerifikasiScreen", "Report: $report")
    if (report != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
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
                        fontSize = 22.sp
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
                            tint = Color.Black,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
                HorizontalDivider(thickness = 2.dp, color = Divider)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Text("Pelapor", color = TextVerifikasi, fontSize = textSize)
                        Text("NIM/NIP", color = TextVerifikasi, fontSize = textSize)
                        Text("No Telp", color = TextVerifikasi, fontSize = textSize)
                        Text("Jurusan", color = TextVerifikasi, fontSize = textSize)
                        Text("Status Pelapor", color = TextVerifikasi, fontSize = textSize)
                        Text("Tempat Kejadian", color = TextVerifikasi, fontSize = textSize)
                        Text("Waktu Kejadian", color = TextVerifikasi, fontSize = textSize)
                        Text("Bentuk Kekerasan", color = TextVerifikasi, fontSize = textSize)
                        Text("Deskripsi Kejadian", color = TextVerifikasi, fontSize = textSize)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Text("Haikal Hariyanto", fontSize = textSize, fontWeight = FontWeight.SemiBold)
                        Text("231511070", fontSize = textSize, fontWeight = FontWeight.SemiBold)
                        Text("095555555", fontSize = textSize, fontWeight = FontWeight.SemiBold)
                        Text("Teknik Komputer", fontSize = textSize, fontWeight = FontWeight.SemiBold)
                        report?.let { report ->
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val tanggalKejadian = LocalDate.parse(report.tanggalKejadian.toString(), formatter)
                            val hariIni = LocalDate.now()
                            val selisihHari = ChronoUnit.DAYS.between(tanggalKejadian, hariIni)

                            Text(
                                if (report.isKorban) "Korban" else "Saksi",
                                fontSize = textSize,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(report.tempatKejadian ?: "-")

                            Text(
                                "${report.tanggalKejadian} (${selisihHari} hari yang lalu)",
                                fontSize = textSize,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                report.jenisKekerasan.toReadableString(),
                                fontSize = textSize,
                                fontWeight = FontWeight.SemiBold
                            )

                            TextField(
                                value = report.deskripsi ?: "",
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .border(2.dp, Divider, shape = MaterialTheme.shapes.small)
                                    .background(Color.White, shape = MaterialTheme.shapes.small),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    disabledContainerColor = Color.Gray
                                )
                            )
                        } ?: Text("Data laporan tidak tersedia.")

                    }
                }

                Text(
                    "Bukti Pendukung",
                    color = TextVerifikasi,
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
            HorizontalDivider(thickness = 2.dp, color = Divider)
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
                    onClick = { viewModel.updateReportStatus(report?.id!!, StatusLaporan.REJECTED) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp),
                    contentColor = Color.Red
                )
                CustomButton(
                    text = "Terima",
                    onClick = { viewModel.updateReportStatus(report?.id!!, StatusLaporan.REJECTED) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Data tidak ditemukan")
        }
    }
}
