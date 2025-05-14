package org.example.project.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.example.project.R
import org.example.project.ui.viewmodel.AuthViewModel
import java.util.regex.Pattern

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val webClientId = stringResource(id = R.string.web_client_id)

    val authState by authViewModel.authState.collectAsState()
    val availableRoles by authViewModel.availableRoles.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var showRoleDialog by remember { mutableStateOf(false) }

    HandleAuthState(
        authState = authState,
        navController = navController,
        onLoadingChange = { isLoading = it },
        onShowRoleDialog = { showRoleDialog = it },
        authViewModel = authViewModel
    )

    if (showRoleDialog && authState !is AuthViewModel.AuthState.FormRequired) {
        RoleSelectionDialog(
            availableRoles = availableRoles,
            selectedRole = selectedRole,
            onRoleSelected = { role ->
                selectedRole = role
                showRoleDialog = false
                authViewModel.handleRoleSelection(authViewModel, role, context, coroutineScope)
            },
            onDismiss = { showRoleDialog = false }
        )
    }

    if (authState !is AuthViewModel.AuthState.FormRequired) {
        LoginContent(
            isLoading = isLoading,
            onLoginClick = {
                authViewModel.startGoogleSignIn(
                    context = context,
                    webClientId = webClientId,
                    onLoadingChanged = { isLoading = it }
                )
            }
        )
    }
}

@Composable
private fun HandleAuthState(
    authState: AuthViewModel.AuthState,
    navController: NavHostController,
    onLoadingChange: (Boolean) -> Unit,
    onShowRoleDialog: (Boolean) -> Unit,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var showForm by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Success -> {
                onLoadingChange(false)
                showForm = false
                Toast.makeText(context, "Login berhasil!", Toast.LENGTH_SHORT).show()
                val destination = when (authState.activeRole) {
                    "satgas" -> "under_development"
                    "ketua" -> "dashboard_ketua"
                    else -> "dashboard_user"
                }
                navController.navigate(destination) {
                    popUpTo("login") { inclusive = true }
                }
            }

            is AuthViewModel.AuthState.Error -> {
                onLoadingChange(false)
                showForm = false
                Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            }

            is AuthViewModel.AuthState.RoleSelectionRequired -> {
                onLoadingChange(false)
                showForm = false
                onShowRoleDialog(true)
            }

            is AuthViewModel.AuthState.Loading -> {
                onLoadingChange(true)
                showForm = false
            }

            is AuthViewModel.AuthState.FormRequired -> {
                showForm = true
                onLoadingChange(false)
            }

            is AuthViewModel.AuthState.Idle -> {
                onLoadingChange(false)
                showForm = false
            }
        }
    }

    if (showForm && authState is AuthViewModel.AuthState.FormRequired) {
        AdditionalDataForm(
            onSave = { alamat, kontak, jenisKelamin ->
                authViewModel.saveUserToFirestore(
                    uid = authState.uid,
                    nama = authState.nama,
                    nim = authState.nim,
                    user = authState.user,
                    kontak = kontak,
                    alamat = alamat,
                    jenisKelamin = jenisKelamin,
                    selectedRole = authState.selectedRole ?: authState.roles.firstOrNull() ?: "User",
                    roles = authState.roles,
                    fcmToken = authState.fcmToken
                )
            }
        )
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun AdditionalDataForm(
    onSave: (String, String, String) -> Unit
) {
    var alamat by remember { mutableStateOf("") }
    var kontak by remember { mutableStateOf("") }
    var jenisKelamin by remember { mutableStateOf("Perempuan") }
    var errorAlamat by remember { mutableStateOf<String?>(null) }
    var errorKontak by remember { mutableStateOf<String?>(null) }

    val phonePattern = Pattern.compile("^08[0-9]{8,11}$")

    var isAlamatFocused by remember { mutableStateOf(false) }
    var isKontakFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.satgas_ppkpt),
                contentDescription = "Logo Satgas",
                modifier = Modifier
                    .height(120.dp)
                    .padding(bottom = 24.dp)
            )

            Text(
                text = "Selamat datang di Aplikasi\nSatgas PPKPT Polban",
                color = Color.Gray,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "*Lengkapi data diri anda",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Text("No Telepon", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(3.dp))
            OutlinedTextField(
                value = kontak,
                onValueChange = {
                    kontak = it
                    errorKontak = when {
                        it.isEmpty() -> "Nomor telepon tidak boleh kosong"
                        !phonePattern.matcher(it).matches() -> "Nomor telepon tidak valid"
                        else -> null
                    }
                },
                isError = errorKontak != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .onFocusChanged { isKontakFocused = it.isFocused },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone Icon",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            errorKontak?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                )
            }

            Spacer(Modifier.height(8.dp))
            Text("Alamat", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(3.dp))
            OutlinedTextField(
                value = alamat,
                onValueChange = {
                    alamat = it
                    errorAlamat = if (it.isEmpty()) "Alamat tidak boleh kosong" else null
                },
                isError = errorAlamat != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .onFocusChanged { isAlamatFocused = it.isFocused },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location Icon",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                shape = RoundedCornerShape(12.dp)
            )
            errorAlamat?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                )
            }

            Spacer(Modifier.height(8.dp))
            Text("Jenis Kelamin", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth())
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Perempuan", "Laki-laki").forEach { gender ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { jenisKelamin = gender }
                            .padding(end = 16.dp)
                    ) {
                        RadioButton(
                            selected = jenisKelamin == gender,
                            onClick = { jenisKelamin = gender },
                            modifier = Modifier.size(20.dp),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = gender)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (errorAlamat == null && errorKontak == null && alamat.isNotEmpty() && kontak.isNotEmpty()) {
                        onSave(alamat, kontak, jenisKelamin)
                    }
                },
                enabled = errorAlamat == null && errorKontak == null && alamat.isNotEmpty() && kontak.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan & Lanjut")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleSelectionDialog(
    availableRoles: List<String>,
    selectedRole: String?,
    onRoleSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Pilih Peran",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Anda memiliki beberapa peran. Pilih peran yang ingin digunakan:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                availableRoles.forEach { role ->
                    RoleSelectionItem(
                        role = role,
                        isSelected = selectedRole == role,
                        onClick = { onRoleSelected(role) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleSelectionItem(
    role: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )
        Text(
            text = role.replaceFirstChar { it.uppercaseChar() },
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun LoginContent(
    isLoading: Boolean,
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.satgas_ppkpt),
                    contentDescription = "Logo Satgas",
                    modifier = Modifier
                        .height(120.dp)
                        .padding(bottom = 24.dp)
                )

                Text(
                    text = "Selamat datang di Aplikasi Satgas PPKPT Polban",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Aplikasi ini merupakan platform resmi untuk pelaporan kekerasan, membaca artikel edukatif, serta membuat janji temu dengan Satgas Pencegahan dan Penanganan Kekerasan Politeknik Negeri Bandung (Polban).",
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "Google Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Login dengan akun Google Polban",
                        color = MaterialTheme.colorScheme.background
                    )
                }

                Spacer(modifier = Modifier.height(1.dp))

                Text(
                    text = "Hanya pengguna dengan akun email Polban (@polban.ac.id) yang dapat login.",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
