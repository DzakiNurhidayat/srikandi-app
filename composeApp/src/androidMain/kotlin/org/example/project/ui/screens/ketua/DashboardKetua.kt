package org.example.project.ui.screens.ketua

import android.R
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import org.example.project.common.enums.StatusLaporan
import org.example.project.data.model.Filter
import org.example.project.model.entities.Report
import org.example.project.ui.components.CustomButton
import org.example.project.ui.components.FilterChip
import org.example.project.ui.viewmodel.ReportViewModel
import org.example.project.ui.viewmodel.VerifikasiViewModel
import org.example.project.utils.shadow
import org.example.project.utils.toReadableString
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: ReportViewModel = hiltViewModel(),
    verifikasiViewModel: VerifikasiViewModel
) {
    val reports by viewModel.reports.collectAsState()
    val isRefreshing = remember { mutableStateOf(false) }
    val selectedFilter = remember { mutableStateOf("All") }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = {
            isRefreshing.value = true
            viewModel.getReports {
                isRefreshing.value = false
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.getReports()
    }
    LaunchedEffect(reports) {
        isRefreshing.value = false
    }

    val filteredReports = filterReports(reports, selectedFilter.value)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            HeaderKetuaSection()
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.secondary)
            TotalCase(filteredReports.size, "Total Kasus")
            FilterKasus(selectedFilter)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 390.dp)
        ) {
            items(filteredReports) { report ->
                CaseCard(navController, verifikasiViewModel, report)
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing.value,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun HeaderKetuaSection() {
    Column(Modifier.padding(24.dp, 0.dp, 24.dp, 12.dp)) {
        Text(
            text = "Keberanian kita adalah harapan bagi mereka yang tak bersuara. Mari terus bekerja dengan semangat untuk menciptakan lingkungan yang aman di dunia pendidikan.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Justify
        )
    }
}

@Composable
fun TotalCase(totalCases: Int, placeholder: String) {
    var sortMode by remember { mutableStateOf(SortMode.INACTIVE) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 24.dp)
            .shadow(
                color = Color.Black.copy(alpha = 0.2f),
                borderRadius = 20.dp,
                blurRadius = 15.dp,
                offsetX = 2.dp,
                offsetY = 8.dp,
                spread = 0.dp
            )
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)

        if (isSearchActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search_category_default),
                                contentDescription = "Search Icon",
                                modifier = Modifier.size(18.dp),
                                tint = if (searchText.isNotEmpty()) Color.Blue else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchText.isEmpty()) {
                                    Text(
                                        text = "Cari...",
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontSize = 15.sp
                                    )
                                }
                                innerTextField()
                            }
                            if (searchText.isNotEmpty()) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_menu_close_clear_cancel),
                                    contentDescription = "Clear Icon",
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {
                                            searchText = ""
                                            isSearchActive = false
                                        },
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                )
            }
        } else {
            // Tampilkan layout awal jika search tidak aktif
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Card(
                    modifier = Modifier
                        .wrapContentSize()
                        .shadow(
                            color = Color.Black.copy(alpha = 0.2f),
                            borderRadius = 20.dp,
                            blurRadius = 5.dp,
                            offsetX = 1.dp,
                            offsetY = 2.dp,
                            spread = 0.dp
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_dialog_info),
                            contentDescription = "Briefcase Icon",
                            modifier = Modifier.size(15.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$totalCases $placeholder",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(
                            id = when (sortMode) {
                                SortMode.INACTIVE -> R.drawable.ic_menu_recent_history
                                SortMode.LATEST -> R.drawable.ic_menu_sort_alphabetically
                                SortMode.OLDEST -> R.drawable.ic_menu_sort_by_size
                            }
                        ),
                        contentDescription = "Sort Icon",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(horizontal = 8.dp)
                            .clickable {
                                sortMode = when (sortMode) {
                                    SortMode.INACTIVE -> SortMode.LATEST
                                    SortMode.LATEST -> SortMode.OLDEST
                                    SortMode.OLDEST -> SortMode.INACTIVE
                                }
                            },
                        tint = MaterialTheme.colorScheme.onSurface
                    )

                    VerticalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.height(24.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_search_category_default),
                        contentDescription = "Search Icon",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(horizontal = 8.dp)
                            .clickable {
                                isSearchActive = true // Tampilkan kolom pencarian saat diklik
                            },
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}


enum class SortMode {
    INACTIVE, // Hanya jam (tidak ada sort)
    LATEST,   // Sort terakhir dibuat (ascending)
    OLDEST    // Sort terlama dibuat (descending)
}

@Composable
fun FilterKasus(
    selectedFilter: MutableState<String>
) {
    val filters = listOf("All", "Assign", "In Progress", "Done")
    LazyRow(
        modifier = Modifier
            .padding(vertical = 5.dp, horizontal = 30.dp)
            .fillMaxWidth()
    ) {
        items(filters) { filter ->
            val isSelected = selectedFilter.value == filter
            FilterChip(
                filter = Filter(
                    name = filter,
                    enabled = remember { mutableStateOf(isSelected) }
                ),
                onSelected = {
                    selectedFilter.value = filter
                },
                modifier = Modifier.padding(end = 5.dp)
            )
        }
    }
}

fun filterReports(reports: List<Report>, selectedFilter: String): List<Report> {
    val filtered = reports.filter { it.statusLaporan != StatusLaporan.REJECTED }
    return when (selectedFilter) {
        "Assign" -> filtered.filter {
            it.statusLaporan in listOf(
                StatusLaporan.DRAFT,
                StatusLaporan.VERIFIED,
                StatusLaporan.DELETED,
                StatusLaporan.TEAMED,
                StatusLaporan.FORM1
            )
        }

        "In Progress" -> filtered.filter { it.statusLaporan == StatusLaporan.FORM2 }
        "Done" -> filtered.filter { it.statusLaporan == StatusLaporan.DONE }
        else -> filtered
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CaseCard(
    navController: NavHostController,
    verifikasiViewModel: VerifikasiViewModel,
    report: Report
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Haikal Hariyanto", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = "Teknik Komputer dan Informatika '23",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
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
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = report.jenisKekerasan.toReadableString(),
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = TextUnit(16f, TextUnitType.Sp)),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedDate,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
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
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = report.tempatKejadian,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = TextUnit(16f, TextUnitType.Sp))
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            val buttonText = when (report.statusLaporan) {
                StatusLaporan.DRAFT, StatusLaporan.DELETED -> "Verifikasi"
                StatusLaporan.VERIFIED -> "Pengisian Form 1"
                StatusLaporan.TEAMED, StatusLaporan.FORM2, StatusLaporan.DONE -> "Detail Laporan"
                else -> "Detail Laporan"
            }
            CustomButton(
                text = buttonText,
                onClick = {
                    verifikasiViewModel.setReport(report)
                    when (report.statusLaporan) {
                        StatusLaporan.DRAFT,
                        StatusLaporan.DELETED -> navController.navigate("verifikasi_kasus")

                        StatusLaporan.VERIFIED -> navController.navigate("under_development")
                        StatusLaporan.TEAMED,
                        StatusLaporan.FORM2,
                        StatusLaporan.DONE -> navController.navigate("under_development")

                        else -> navController.navigate("under_development")
                    }
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