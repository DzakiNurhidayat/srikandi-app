package org.example.project.ui.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.example.project.common.KTOR_SERVER_STATIC_BASE_URL
import org.example.project.data.model.UserProfile
import org.example.project.ui.components.confirmationDialog
import org.example.project.ui.navigation.Screen
import org.example.project.ui.viewmodel.AuthViewModel
import org.example.project.ui.viewmodel.ProfileViewModel
import org.example.project.ui.viewmodel.ProfileViewModel.UserProfileUiState

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by profileViewModel.userProfileUiState.collectAsState()
    val imageUploadUiState by profileViewModel.imageUploadState.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileViewModel.updateProfilePicture(it)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (val state = uiState) {
            is UserProfileUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is UserProfileUiState.Success -> {
                ProfileScreenContent(
                    navController = navController,
                    authViewModel = authViewModel,
                    userProfile = state.userProfile,
                    context = context,
                    onImagePickerClick = { imagePickerLauncher.launch("image/*") },
                    imageUploadState = imageUploadUiState
                )
            }

            is UserProfileUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { profileViewModel.loadUserProfile() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreenContent(
    navController: NavController,
    authViewModel: AuthViewModel,
    userProfile: UserProfile,
    context: Context,
    onImagePickerClick: () -> Unit,
    imageUploadState: ProfileViewModel.ImageUploadUiState
) {
    val scale = remember { Animatable(0f) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
    }
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 160.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EditableProfileImage(
                        profileImageUrl = userProfile.fotoProfil,
                        imageUploadState = imageUploadState,
                        scaleValue = scale.value,
                        onImagePickerClick = onImagePickerClick,
                        context = context
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = userProfile.nama.ifEmpty { "Nama Pengguna" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
            Card(
                modifier = Modifier
                    .offset(y = (-40).dp)
                    .fillMaxWidth(0.85f),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Laporan Terakhir",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "Selesai",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Jumlah Laporan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "12",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(icon = Icons.Default.Code, text = userProfile.nim.ifEmpty { "NIM tidak tersedia" })
            InfoRow(icon = Icons.Default.Email, text = userProfile.email ?: "Email tidak tersedia")
            InfoRow(icon = Icons.Default.Person, text = userProfile.activeRole.ifEmpty { "Peran tidak diketahui" })
            InfoRow(icon = Icons.Filled.School, text = userProfile.jurusan.ifEmpty { "Jurusan tidak diketahui" })
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 24.dp, end = 24.dp, bottom = 16.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    Toast.makeText(context, "Fitur FAQ belum ztersedia", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Help,
                        contentDescription = "FAQ Icon",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FAQ",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout Icon",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    if (showLogoutDialog) {
        confirmationDialog(
            title = "Konfirmasi Logout",
            message = "Apakah Anda yakin ingin logout?",
            confirmText = "Logout",
            confirmButtonColor = MaterialTheme.colorScheme.error,
            onConfirm = {
                showLogoutDialog = false
                authViewModel.logout { success ->
                    if (success) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                        Toast.makeText(context, "Logout berhasil!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Logout gagal! Coba lagi.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDismiss = { showLogoutDialog = false },
            dismissText = "Batal",
            icon = Icons.Default.ExitToApp
        )
    }

}

@Composable
private fun EditableProfileImage(
    profileImageUrl: String,
    imageUploadState: ProfileViewModel.ImageUploadUiState,
    scaleValue: Float,
    onImagePickerClick: () -> Unit,
    context: Context
) {
    val ktorStaticBaseUrl = KTOR_SERVER_STATIC_BASE_URL

    val displayableImageUrl = remember(profileImageUrl) {
        when {
            profileImageUrl.isBlank() -> ""
            profileImageUrl.startsWith("http://") || profileImageUrl.startsWith("https://") -> {
                profileImageUrl
            }

            else -> {
                ktorStaticBaseUrl + "profile_pictures/" + profileImageUrl
            }
        }
    }
    Box(
        modifier = Modifier.size(130.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scaleValue)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (displayableImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = displayableImageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    onError = { error ->
                        Log.e(
                            "EditableProfileImage",
                            "Error loading image: $displayableImageUrl",
                            error.result.throwable
                        )
                    }
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Placeholder",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (imageUploadState is ProfileViewModel.ImageUploadUiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f))
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        IconButton(
            onClick = onImagePickerClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(40.dp) // Ukuran tombol
                .offset(x = (4).dp, y = (4).dp)
                .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Ganti Foto Profil",
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (imageUploadState is ProfileViewModel.ImageUploadUiState.Error) {
        LaunchedEffect(imageUploadState.message) {
            Toast.makeText(context, "Gagal unggah: ${imageUploadState.message}", Toast.LENGTH_SHORT).show()
        }
    }

    if (imageUploadState is ProfileViewModel.ImageUploadUiState.Success) {
        LaunchedEffect(imageUploadState.serverPath) {
            Toast.makeText(context, "Foto profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
        }
    }

}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Normal
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun ProfileScreenPreview() {
//    MaterialTheme {
//        ProfileScreen(
//            onLogout = {},
//            onFAQClick = {}
//        )
//    }
//}