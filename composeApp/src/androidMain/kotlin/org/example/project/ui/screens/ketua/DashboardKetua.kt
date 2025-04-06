package org.example.project.ui.screens.ketua

import android.R
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.example.project.data.model.Filter
import org.example.project.model.entities.Report
import org.example.project.ui.components.CustomButton
import org.example.project.ui.components.FilterChip
import org.example.project.ui.theme.Divider
import org.example.project.ui.viewmodel.ReportViewModel
import org.example.project.ui.viewmodel.shared.SharedReportViewModel
import org.example.project.utils.shadow
import org.example.project.utils.toReadableString
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: ReportViewModel = hiltViewModel(),
    sharedViewModel: SharedReportViewModel
) {
    val reports by viewModel.reports.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HeaderSection()
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 2.dp, color = Divider)
        TotalCase(reports.size)
        FilterKasus()

        val formattedReports = reports
        LazyColumn {
            items(formattedReports) { report ->
                CaseCard(navController, report)
            }
        }

    }
}

@Composable
fun HeaderSection() {
    Column(Modifier.padding(30.dp, 40.dp, 30.dp, 10.dp)) {
        Text(text = "Selamat Bertugas,", style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "Haikal Al Jufri", fontSize = 20.sp, fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Keberanian kita adalah harapan bagi mereka yang tak bersuara. Mari terus bekerja dengan semangat untuk menciptakan lingkungan yang aman di dunia pendidikan",
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Justify
        )
    }
}

@Composable
fun TotalCase(totalCases: Int) {
    Column(
        modifier = Modifier
            .padding(vertical = 20.dp, horizontal = 30.dp)
            .shadow(
                color = Color.Black.copy(alpha = 0.2f),
                borderRadius = 20.dp,
                blurRadius = 15.dp,
                offsetX = 2.dp,
                offsetY = 8.dp,
                spread = 0.dp
            )
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
    ) {
        Spacer(modifier = Modifier.height(70.dp))
        HorizontalDivider(thickness = 1.dp, color = Color.Black)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(50.dp))
                    .padding(horizontal = 15.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier.wrapContentSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_dialog_info),
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$totalCases Total Kasus", fontSize = 15.sp, fontWeight = FontWeight.Bold
                    )
                }
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_menu_recent_history),
                contentDescription = "Time Icon",
                modifier = Modifier
                    .size(55.dp)
                    .padding(horizontal = 15.dp),
                tint = Color.Black
            )
        }
    }
}

@Composable
fun FilterKasus() {
    val filters = remember {
        listOf(
            Filter(name = "All", enabled = mutableStateOf(true)),
            Filter(name = "Assign", enabled = mutableStateOf(false)),
            Filter(name = "In Progress", enabled = mutableStateOf(false)),
            Filter(name = "Done", enabled = mutableStateOf(false))
        )
    }

    LazyRow(
        modifier = Modifier
            .padding(vertical = 5.dp, horizontal = 30.dp)
            .fillMaxWidth()
    ) {
        items(filters.size) { index ->
            val filter = filters[index]

            FilterChip(
                filter = filter,
                onSelected = { selectedFilter ->
                    filters.forEach { it.enabled.value = it == selectedFilter }
                },
                modifier = Modifier
                    .padding(end = 5.dp)
            )
        }
    }
}

// DashboardKetua.kt
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CaseCard(
    navController: NavHostController,
    report: Report,
    sharedViewModel: SharedReportViewModel = hiltViewModel()
) {
    val formattedDate = remember(report.tanggalKejadian) {
        report.tanggalKejadian.format(DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale("id")))
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 15.dp)
            .shadow(
                color = Color.Black.copy(alpha = 0.2f),
                borderRadius = 20.dp,
                blurRadius = 15.dp,
                offsetX = 2.dp,
                offsetY = 8.dp,
                spread = 0.dp
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Haikal Hariyanto", fontWeight = FontWeight.Bold)
            Text(text = "Teknik Komputer dan Informatika '23", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.stat_sys_warning),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = report.jenisKekerasan.toReadableString(),
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = TextUnit(16f, TextUnitType.Sp))
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedDate,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = TextUnit(16f, TextUnitType.Sp))
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = report.tempatKejadian,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = TextUnit(16f, TextUnitType.Sp))
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            CustomButton(
                text = "Verifikasi",
                onClick = {
                    sharedViewModel.setReport(report)
                    navController.navigate("verifikasi_kasus")
                },
                modifier = Modifier.padding(16.dp),
                borderRadius = 50
            )
        }
    }
}

//@Composable
//fun BottomNavigationBar() {
//    BottomAppBar(containerColor = Color.White) {
//        IconButton(onClick = {}) {
//            Icon(painter = painterResource(id = android.R.drawable.ic_menu_compass), contentDescription = "Home")
//        }
//        Spacer(modifier = Modifier.weight(1f))
//        IconButton(onClick = {}) {
//            Icon(painter = painterResource(id = android.R.drawable.ic_input_add), contentDescription = "Add")
//        }
//        Spacer(modifier = Modifier.weight(1f))
//        IconButton(onClick = {}) {
//            Icon(
//                painter = painterResource(id = android.R.drawable.ic_menu_info_details), contentDescription = "Profile"
//            )
//        }
//    }
//}