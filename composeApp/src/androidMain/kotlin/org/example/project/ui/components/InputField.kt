package org.example.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*
import org.example.project.ui.theme.altSurfaceVariant
import org.example.project.ui.theme.surfaceVariant

interface ReadableEnum {
    fun toReadableString(): String
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownMenuBox(
    selected: T,
    onItemSelected: (T) -> Unit,
    items: List<T>,
    label: String = "Pilih Item"
) where T : Enum<T>, T : ReadableEnum {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selected.toReadableString(),
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = Color.Black) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = altSurfaceVariant,
                unfocusedBorderColor = surfaceVariant
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerTextField(
    modifier: Modifier = Modifier,
    onDateSelected: (String) -> Unit = {}
) {
    var waktuKejadianDisplay by remember { mutableStateOf("") }
    var waktuKejadianServer by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val serverFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    OutlinedTextField(
        value = waktuKejadianDisplay,
        onValueChange = { /* Tidak ada aksi karena hanya untuk tampilan */ },
        label = { Text("Tanggal", color = Color.Black) },
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = { showDatePicker = true },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = Color.Gray
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledBorderColor = Color.Gray,
            disabledTextColor = Color.Black
        ),
        enabled = false
    )

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        val todayMillis = calendar.timeInMillis
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = calendar.timeInMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= todayMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Date(millis)
                            waktuKejadianServer = serverFormat.format(selectedDate)
                            waktuKejadianDisplay = displayFormat.format(selectedDate)
                            onDateSelected(waktuKejadianServer)
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = surfaceVariant
                    )
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Text("Batal")
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                titleContentColor = surfaceVariant,
                headlineContentColor = surfaceVariant,
                weekdayContentColor = Color.Black,
                subheadContentColor = surfaceVariant,
                yearContentColor = Color.Black,
                currentYearContentColor = Color(0xFF3F51B5),
                selectedYearContainerColor = Color(0xFF3F51B5),
                selectedYearContentColor = surfaceVariant,
                dayContentColor = Color.Black,
                selectedDayContainerColor = Color(0xFF3F51B5),
                selectedDayContentColor = surfaceVariant,
                todayContentColor = Color(0xFF3F51B5),
                todayDateBorderColor = Color(0xFF3F51B5),
                disabledDayContentColor = Color.Gray,
                navigationContentColor = surfaceVariant
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }
}