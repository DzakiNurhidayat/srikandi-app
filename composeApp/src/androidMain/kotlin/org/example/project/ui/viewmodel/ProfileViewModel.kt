package org.example.project.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.example.project.data.model.UserProfile
import org.example.project.data.repositories.UserRepository
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val application: Application

) : ViewModel() {

    private val _userProfileUiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)
    val userProfileUiState: StateFlow<UserProfileUiState> = _userProfileUiState.asStateFlow()

    private val _imageUploadState = MutableStateFlow<ImageUploadUiState>(ImageUploadUiState.Idle)
    val imageUploadState: StateFlow<ImageUploadUiState> = _imageUploadState.asStateFlow()

    sealed interface UserProfileUiState {
        object Loading : UserProfileUiState
        data class Success(val userProfile: UserProfile) : UserProfileUiState
        data class Error(val message: String) : UserProfileUiState
    }

    sealed interface ImageUploadUiState {
        object Idle : ImageUploadUiState
        object Loading : ImageUploadUiState
        data class Success(val serverPath: String) : ImageUploadUiState
        data class Error(val message: String) : ImageUploadUiState
    }

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        _userProfileUiState.value = UserProfileUiState.Loading
        viewModelScope.launch {
            repository.getUserProfileData()
                .collect { result ->
                    result.fold(
                        onSuccess = { userProfile ->
                            _userProfileUiState.value = UserProfileUiState.Success(userProfile)
                        },
                        onFailure = { exception ->
                            _userProfileUiState.value = UserProfileUiState.Error(
                                exception.message ?: "Gagal memuat data profil"
                            )
                        }
                    )
                }
        }
    }

    fun updateProfilePicture(imageUri: Uri) {
        _imageUploadState.value = ImageUploadUiState.Loading
        viewModelScope.launch {
            repository.getUserProfileData().collect { userProfileResult ->
                userProfileResult.fold(
                    onSuccess = { userProfile ->
                        if (userProfile.uid.isEmpty()) { // Periksa apakah UID valid
                            _imageUploadState.value = ImageUploadUiState.Error("UID pengguna tidak ditemukan.")
                            return@fold
                        }

                        try {
                            val imageFilePart = prepareFilePart("image", imageUri)
                            if (imageFilePart == null) {
                                _imageUploadState.value = ImageUploadUiState.Error("Gagal memproses file gambar.")
                                return@fold
                            }

                            viewModelScope.launch {
                                repository.uploadProfileImageToServer(imageFilePart)
                                    .collect { uploadResult ->
                                        uploadResult.fold(
                                            onSuccess = { serverPath ->
                                                updateUserPhotoPathInFirestore(
                                                    userProfile.uid,
                                                    serverPath
                                                )
                                            },
                                            onFailure = { exception ->
                                                _imageUploadState.value = ImageUploadUiState.Error(
                                                    exception.message ?: "Gagal mengunggah gambar ke server."
                                                )
                                            }
                                        )
                                    }
                            }
                        } catch (e: Exception) {
                            _imageUploadState.value = ImageUploadUiState.Error(
                                e.message ?: "Terjadi kesalahan saat persiapan unggah."
                            )
                        }
                    },
                    onFailure = { exception ->
                        _imageUploadState.value = ImageUploadUiState.Error(
                            exception.message ?: "Gagal mendapatkan data pengguna."
                        )
                    }
                )
            }
        }
    }

    private fun updateUserPhotoPathInFirestore(userId: String, serverPath: String) {
        _imageUploadState.value = ImageUploadUiState.Loading
        viewModelScope.launch {
            repository.updateUserProfilePhotoPath(userId, serverPath)
                .collect { firestoreUpdateResult ->
                    firestoreUpdateResult.fold(
                        onSuccess = {
                            _imageUploadState.value = ImageUploadUiState.Success(serverPath)
                            loadUserProfile()
                        },
                        onFailure = { exception ->
                            _imageUploadState.value = ImageUploadUiState.Error(
                                exception.message ?: "Gagal memperbarui path foto di Firestore."
                            )
                        }
                    )
                }
        }
    }

    private fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part? {
        return try {
            val contentResolver = application.contentResolver
            val inputStream = contentResolver.openInputStream(fileUri) ?: return null

            var fileName = "uploaded_image"
            val cursor = contentResolver.query(fileUri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        fileName = it.getString(displayNameIndex)
                    }
                }
            }
            val requestFile = inputStream.readBytes().toRequestBody(
                contentResolver.getType(fileUri)?.toMediaTypeOrNull()
            )
            inputStream.close()
            MultipartBody.Part.createFormData(partName, fileName, requestFile)
        } catch (e: IOException) {
            Log.e("ProfileViewModel", "IOException while preparing file part: ${e.message}", e)
            null
        }
    }

    fun refreshUserProfile() {
        loadUserProfile()
    }

    // Jika Anda ingin menambahkan logika update profil, fungsi-fungsinya akan ada di sini,
    // memanggil metode update di authRepository.
    // Contoh:
    // fun updateNama(newName: String) {
    //     viewModelScope.launch {
    //         _userProfileUiState.value = UserProfileUiState.Loading // Opsional: tunjukkan loading saat update
    //         try {
    //             val currentUser = authRepository.getCurrentUser()
    //             if (currentUser != null) {
    //                 authRepository.updateUserName(currentUser.uid, newName) // Perlu metode ini di repository
    //                 loadUserProfile() // Muat ulang profil setelah update
    //             } else {
    //                 _userProfileUiState.value = UserProfileUiState.Error("Sesi pengguna tidak ditemukan untuk update.")
    //             }
    //         } catch (e: Exception) {
    //             _userProfileUiState.value = UserProfileUiState.Error("Gagal mengupdate nama: ${e.message}")
    //         }
    //     }
    // }
}