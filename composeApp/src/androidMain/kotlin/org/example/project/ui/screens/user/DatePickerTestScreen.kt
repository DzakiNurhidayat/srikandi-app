import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerTextField(
    modifier: Modifier = Modifier,
    onDateSelected: (String) -> Unit = {}
) {
    var waktuKejadian by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    OutlinedTextField(
        value = waktuKejadian,
        onValueChange = { /* Tidak ada aksi karena hanya untuk tampilan */ },
        label = { Text("Tanggal") },
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        enabled = false,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null
            )
        }
    )

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = calendar.timeInMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Date(millis)
                            waktuKejadian = dateFormat.format(selectedDate)
                            onDateSelected(waktuKejadian)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun SimpleDatePickerScreen() {
    var selectedDate by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Pilih Tanggal",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        DatePickerTextField(
            modifier = Modifier.weight(1f),
            onDateSelected = { date ->
                selectedDate = date
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tanggal Terpilih: $selectedDate",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}