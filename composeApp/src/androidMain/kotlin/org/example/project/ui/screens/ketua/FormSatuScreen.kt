package org.example.project.ui.screens.ketua

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.ui.components.AppTopBar

@Composable
fun FormSatuScreen(
    onNavigateBack: () -> Unit,
    onSubmit: (FormSatuData) -> Unit
) {
    val scrollState = rememberScrollState()

    var namaPelapor by remember { mutableStateOf("") }
    var nomorTelepon by remember { mutableStateOf("") }
    var jenisPelapor by remember { mutableStateOf("") }
    var domisiliPelapor by remember { mutableStateOf("") }
    var jenisKekerasanSeksual by remember { mutableStateOf("") }
    var ceritaSingkat by remember { mutableStateOf("") }
    var ciriFisik by remember { mutableStateOf("") }
    var memilikiDisabilitas by remember { mutableStateOf(false) }
    var statusTerlapor by remember { mutableStateOf("") }
    var jenisKelaminTerlapor by remember { mutableStateOf("") }

    // Alasan pengaduan multiple choices
    var alasanSaksi by remember { mutableStateOf(false) }
    var alasanKorbanButuhBantuan by remember { mutableStateOf(false) }
    var alasanTindakTegas by remember { mutableStateOf(false) }
    var alasanDokumentasi by remember { mutableStateOf(false) }
    var alasanLainnya by remember { mutableStateOf(false) }
    var alasanLainnyaText by remember { mutableStateOf("") }

    var kontakLain by remember { mutableStateOf("") }

    // Kebutuhan korban multiple choices
    var kebutuhanKonseling by remember { mutableStateOf(false) }
    var kebutuhanRohani by remember { mutableStateOf(false) }
    var kebutuhanHukum by remember { mutableStateOf(false) }
    var kebutuhanMedis by remember { mutableStateOf(false) }
    var kebutuhanDigital by remember { mutableStateOf(false) }
    var kebutuhanLainnya by remember { mutableStateOf(false) }
    var kebutuhanLainnyaText by remember { mutableStateOf("") }
    var tidakButuhPendampingan by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Formulir Penerimaan Laporan",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error message if any
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Form fields
            FormSection(title = "Data Pelapor") {
                OutlinedTextField(
                    value = namaPelapor,
                    onValueChange = { namaPelapor = it },
                    label = { Text("Nama pelapor (korban/saksi)*") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Radio button for role selection
                Text("Melapor Sebagai:", fontSize = 16.sp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = jenisPelapor == "Korban",
                        onClick = { jenisPelapor = "Korban" }
                    )
                    Text("Korban", modifier = Modifier.padding(start = 4.dp))

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = jenisPelapor == "Saksi",
                        onClick = { jenisPelapor = "Saksi" }
                    )
                    Text("Saksi", modifier = Modifier.padding(start = 4.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nomorTelepon,
                    onValueChange = { nomorTelepon = it },
                    label = { Text("Nomor telepon") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = domisiliPelapor,
                    onValueChange = { domisiliPelapor = it },
                    label = { Text("Domisili pelapor") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            FormSection(title = "Detail Peristiwa") {
                OutlinedTextField(
                    value = jenisKekerasanSeksual,
                    onValueChange = { jenisKekerasanSeksual = it },
                    label = { Text("Jenis kekerasan*") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ceritaSingkat,
                    onValueChange = { ceritaSingkat = it },
                    label = { Text("Isi Laporan") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ceritaSingkat,
                    onValueChange = { ceritaSingkat = it },
                    label = { Text("Cerita singkat peristiwa (waktu, tempat, dan peristiwa)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ciriFisik,
                    onValueChange = { ciriFisik = it },
                    label = { Text("Sebutkan ciri fisik pada saat kejadian") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Disabilitas selection
                Text("Memiliki disabilitas*")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = memilikiDisabilitas,
                        onClick = { memilikiDisabilitas = true }
                    )
                    Text("Ya", modifier = Modifier.padding(start = 4.dp))

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = !memilikiDisabilitas,
                        onClick = { memilikiDisabilitas = false }
                    )
                    Text("Tidak", modifier = Modifier.padding(start = 4.dp))
                }
            }

            FormSection(title = "Data Terlapor") {
                Text("Status terlapor:")

                // Status terlapor options
                val statusOptions = listOf("mahasiswa", "pendidik", "tenaga kependidikan", "warga kampus", "masyarakat umum")
                Column {
                    statusOptions.forEach { status ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = statusTerlapor == status,
                                onClick = { statusTerlapor = status }
                            )
                            Text(status.replaceFirstChar { it.uppercase() }, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Jenis kelamin terlapor
                Text("Jenis kelamin terlapor:")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = jenisKelaminTerlapor == "Laki-laki",
                        onClick = { jenisKelaminTerlapor = "Laki-laki" }
                    )
                    Text("Laki-laki", modifier = Modifier.padding(start = 4.dp))

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = jenisKelaminTerlapor == "Perempuan",
                        onClick = { jenisKelaminTerlapor = "Perempuan" }
                    )
                    Text("Perempuan", modifier = Modifier.padding(start = 4.dp))
                }
            }

            FormSection(title = "Alasan Pengaduan") {
                Text("Silakan centang satu atau lebih pilihan berikut:")

                CheckboxWithLabel(
                    checked = alasanSaksi,
                    onCheckedChange = { alasanSaksi = it },
                    label = "Saya seorang saksi yang khawatir dengan keadaan korban."
                )

                CheckboxWithLabel(
                    checked = alasanKorbanButuhBantuan,
                    onCheckedChange = { alasanKorbanButuhBantuan = it },
                    label = "Saya seorang korban yang memerlukan bantuan pemulihan."
                )

                CheckboxWithLabel(
                    checked = alasanTindakTegas,
                    onCheckedChange = { alasanTindakTegas = it },
                    label = "Saya ingin perguruan tinggi menindak tegas terlapor."
                )

                CheckboxWithLabel(
                    checked = alasanDokumentasi,
                    onCheckedChange = { alasanDokumentasi = it },
                    label = "Saya ingin satuan tugas PPKS mendokumentasikan kejadiannya, meningkatkan keamanan perguruan tinggi dari kekerasan seksual, dan memberi perlindungan bagi saya."
                )

                CheckboxWithLabel(
                    checked = alasanLainnya,
                    onCheckedChange = { alasanLainnya = it },
                    label = "Lainnya:"
                )

                if (alasanLainnya) {
                    OutlinedTextField(
                        value = alasanLainnyaText,
                        onValueChange = { alasanLainnyaText = it },
                        label = { Text("Sebutkan...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            FormSection(title = "Informasi Tambahan") {
                OutlinedTextField(
                    value = kontakLain,
                    onValueChange = { kontakLain = it },
                    label = { Text("Nomor telepon/alamat pos-el pihak lain yang dapat dikonfirmasi") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Identifikasi kebutuhan korban:")

                CheckboxWithLabel(
                    checked = kebutuhanKonseling,
                    onCheckedChange = { kebutuhanKonseling = it },
                    label = "Konseling psikologis"
                )

                CheckboxWithLabel(
                    checked = kebutuhanRohani,
                    onCheckedChange = { kebutuhanRohani = it },
                    label = "Konseling rohani/spiritual"
                )

                CheckboxWithLabel(
                    checked = kebutuhanHukum,
                    onCheckedChange = { kebutuhanHukum = it },
                    label = "Bantuan hukum"
                )

                CheckboxWithLabel(
                    checked = kebutuhanMedis,
                    onCheckedChange = { kebutuhanMedis = it },
                    label = "Bantuan medis"
                )

                CheckboxWithLabel(
                    checked = kebutuhanDigital,
                    onCheckedChange = { kebutuhanDigital = it },
                    label = "Bantuan digital"
                )

                CheckboxWithLabel(
                    checked = kebutuhanLainnya,
                    onCheckedChange = { kebutuhanLainnya = it },
                    label = "Lainnya:"
                )

                if (kebutuhanLainnya) {
                    OutlinedTextField(
                        value = kebutuhanLainnyaText,
                        onValueChange = { kebutuhanLainnyaText = it },
                        label = { Text("Sebutkan...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                CheckboxWithLabel(
                    checked = tidakButuhPendampingan,
                    onCheckedChange = { tidakButuhPendampingan = it },
                    label = "Tidak membutuhkan pendampingan"
                )
            }

            // Upload evidence section
            FormSection(title = "Bukti Pendukung") {
                Text("Upload Bukti Pendukung (opsional)")

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    // Upload buttons/placeholders
                    repeat(3) {
                        OutlinedButton(
                            onClick = { /* Handle upload */ },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Text("+")
                        }
                    }
                }
            }

            // Checkbox for agreement
            CheckboxWithLabel(
                checked = false,
                onCheckedChange = { /* handle check */ },
                label = "Dengan melaporkan kasus, Anda setuju untuk dipanggil oleh tim Satgas guna wawancara lebih lanjut sesuai ketentuan Permendikbud No. 55 Tahun 2024."
            )

            // Submit button
            Button(
                onClick = {
                    // Validate form
                    if (namaPelapor.isBlank() || jenisKekerasanSeksual.isBlank()) {
                        errorMessage = "Kolom bertanda * wajib diisi."
                    } else {
                        // Create form data object and submit
                        val formData = FormSatuData(
                            namaPelapor = namaPelapor,
                            nomorTelepon = nomorTelepon,
                            jenisPelapor = jenisPelapor,
                            domisiliPelapor = domisiliPelapor,
                            jenisKekerasanSeksual = jenisKekerasanSeksual,
                            ceritaSingkat = ceritaSingkat,
                            ciriFisik = ciriFisik,
                            memilikiDisabilitas = memilikiDisabilitas,
                            statusTerlapor = statusTerlapor,
                            jenisKelaminTerlapor = jenisKelaminTerlapor,
                            alasanPengaduan = buildAlasanPengaduan(
                                alasanSaksi, alasanKorbanButuhBantuan, alasanTindakTegas,
                                alasanDokumentasi, alasanLainnya, alasanLainnyaText
                            ),
                            kontakLain = kontakLain,
                            kebutuhanKorban = buildKebutuhanKorban(
                                kebutuhanKonseling, kebutuhanRohani, kebutuhanHukum,
                                kebutuhanMedis, kebutuhanDigital, kebutuhanLainnya,
                                kebutuhanLainnyaText, tidakButuhPendampingan
                            )
                        )
                        onSubmit(formData)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Kirim Laporan")
            }
        }
    }
}

@Composable
fun FormSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun CheckboxWithLabel(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

// Helper functions for building composite data
private fun buildAlasanPengaduan(
    alasanSaksi: Boolean,
    alasanKorbanButuhBantuan: Boolean,
    alasanTindakTegas: Boolean,
    alasanDokumentasi: Boolean,
    alasanLainnya: Boolean,
    alasanLainnyaText: String
): List<String> {
    val alasan = mutableListOf<String>()

    if (alasanSaksi) alasan.add("Saya seorang saksi yang khawatir dengan keadaan korban.")
    if (alasanKorbanButuhBantuan) alasan.add("Saya seorang korban yang memerlukan bantuan pemulihan.")
    if (alasanTindakTegas) alasan.add("Saya ingin perguruan tinggi menindak tegas terlapor.")
    if (alasanDokumentasi) alasan.add("Saya ingin satuan tugas PPKS mendokumentasikan kejadiannya.")
    if (alasanLainnya && alasanLainnyaText.isNotBlank()) alasan.add("Lainnya: $alasanLainnyaText")

    return alasan
}

private fun buildKebutuhanKorban(
    kebutuhanKonseling: Boolean,
    kebutuhanRohani: Boolean,
    kebutuhanHukum: Boolean,
    kebutuhanMedis: Boolean,
    kebutuhanDigital: Boolean,
    kebutuhanLainnya: Boolean,
    kebutuhanLainnyaText: String,
    tidakButuhPendampingan: Boolean
): List<String> {
    val kebutuhan = mutableListOf<String>()

    if (kebutuhanKonseling) kebutuhan.add("Konseling psikologis")
    if (kebutuhanRohani) kebutuhan.add("Konseling rohani/spiritual")
    if (kebutuhanHukum) kebutuhan.add("Bantuan hukum")
    if (kebutuhanMedis) kebutuhan.add("Bantuan medis")
    if (kebutuhanDigital) kebutuhan.add("Bantuan digital")
    if (kebutuhanLainnya && kebutuhanLainnyaText.isNotBlank()) kebutuhan.add("Lainnya: $kebutuhanLainnyaText")
    if (tidakButuhPendampingan) kebutuhan.add("Tidak membutuhkan pendampingan")

    return kebutuhan
}

// Data class to hold form data
data class FormSatuData(
    val namaPelapor: String,
    val nomorTelepon: String,
    val jenisPelapor: String,
    val domisiliPelapor: String,
    val jenisKekerasanSeksual: String,
    val ceritaSingkat: String,
    val ciriFisik: String,
    val memilikiDisabilitas: Boolean,
    val statusTerlapor: String,
    val jenisKelaminTerlapor: String,
    val alasanPengaduan: List<String>,
    val kontakLain: String,
    val kebutuhanKorban: List<String>,
)