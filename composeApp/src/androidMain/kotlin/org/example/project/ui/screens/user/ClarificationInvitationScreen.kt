/*
package org.example.project.ui.screens.user

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.project.R
import org.example.project.common.enums.StatusLaporan
import org.example.project.model.entities.Report
import org.example.project.ui.components.CustomButton
import org.example.project.ui.components.confirmationDialog
import org.example.project.ui.navigation.Screen
import org.example.project.ui.viewmodel.ReportViewModel
import org.example.project.ui.viewmodel.VerifikasiViewModel
import org.example.project.utils.shadow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClarificationInvitationScreen(
    navController: NavHostController,
    verifikasiViewModel: VerifikasiViewModel,
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val report by verifikasiViewModel.report.collectAsState()
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    var showRejectDialog by remember { mutableStateOf(false) }
    var showAcceptDialog by remember { mutableStateOf(false) }

    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePickerDialog = false
                }) {
                    Text("Pilih")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (report != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Undangan Klarifikasi",
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
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(24.dp))

            // Invitation Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
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
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Top tag (Kekerasan Seksual)
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = report!!.jenisKekerasan.toReadableString(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.satgas_ppkpt), // Replace with appropriate image
                            contentDescription = "Notification Icon",
                            modifier = Modifier.size(70.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Laporan ${report!!.idLaporan}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "Detail Laporan ...", // This text seems static, adjust if needed
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Laporan anda telah selesai di verifikasi!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Anda memiliki waktu hingga 14 hari untuk menerima undangan. Jika melewati batas waktu tersebut, kami akan menganggap Anda menolak proses selanjutnya.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Tentukan Tanggal Klarifikasi",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Date Picker Input Field
                    OutlinedTextField(
                        value = selectedDate?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id"))) ?: "",
                        onValueChange = { */
/* Do nothing, handled by date picker *//*
 },
                        label = { Text("Tanggal Klarifikasi") },
                        readOnly = true, // Make it read-only
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Pilih Tanggal",
                                modifier = Modifier.clickable { showDatePickerDialog = true }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textColor = MaterialTheme.colorScheme.onBackground,
                            disabledTextColor = MaterialTheme.colorScheme.onBackground, // Make sure disabled text is visible
                            disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Push buttons to bottom

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CustomButton(
                    text = "Tolak Undangan",
                    onClick = { showRejectDialog = true },
                    modifier = Modifier.weight(1f),
                    borderRadius = 50,
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.error,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                )
                Spacer(modifier = Modifier.width(16.dp))
                CustomButton(
                    text = "Terima Undangan",
                    onClick = {
                        if (selectedDate != null) {
                            showAcceptDialog = true
                        } else {
                            // Show a toast or snackbar to prompt user to select a date
                            // For simplicity, I'm omitting toast/snackbar here.
                            // In a real app, you'd show a message like "Pilih tanggal klarifikasi terlebih dahulu."
                        }
                    },
                    modifier = Modifier.weight(1f),
                    borderRadius = 50,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tidak ada undangan klarifikasi yang dipilih.")
        }
    }

    // Dialogs for Reject/Accept
    if (showRejectDialog) {
        confirmationDialog(
            title = "Tolak Undangan",
            message = "Apakah Anda yakin ingin menolak undangan klarifikasi ini?",
            confirmText = "Tolak",
            confirmButtonColor = MaterialTheme.colorScheme.error,
            onConfirm = {
                showRejectDialog = false
                navController.currentBackStackEntry?.let { entry ->
                    entry.lifecycleScope.launch {
                        report?.id?.let {
                            reportViewModel.updateReport(it, StatusLaporan.REJECTED) // New status for rejected invitation
                        }
                        navController.navigate(Screen.DashboardUser.route) {
                            popUpTo(Screen.DashboardUser.route) { inclusive = true }
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
            title = "Terima Undangan",
            message = "Apakah Anda yakin ingin menerima undangan klarifikasi pada tanggal ${selectedDate?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id")))}?",
            confirmText = "Terima",
            confirmButtonColor = MaterialTheme.colorScheme.primary,
            onConfirm = {
                showAcceptDialog = false
                navController.currentBackStackEntry?.let { entry ->
                    entry.lifecycleScope.launch {
                        report?.id?.let {
                            // Update status to SCHEDULED and save clarification date
                            reportViewModel.updateReportWithClarificationDate(
                                it,
                                StatusLaporan.SCHEDULED, // New status for accepted invitation
                                selectedDate!! // Safe to use !! because we check for null before showing dialog
                            )
                        }
                        navController.navigate(Screen.DashboardUser.route) {
                            popUpTo(Screen.DashboardUser.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            },
            onDismiss = { showAcceptDialog = false },
            dismissText = "Batal",
            icon = Icons.Default.Done
        )
    }
}*/
