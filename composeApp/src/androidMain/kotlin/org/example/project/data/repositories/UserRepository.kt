package org.example.project.data.repositories

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import org.example.project.data.model.UserProfile
import org.example.project.data.remote.ApiService
import org.example.project.data.remote.AuthDataSource
import org.example.project.data.remote.UserDataSource
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource,
    private val apiService: ApiService
) {
    fun getUserProfileData(): Flow<Result<UserProfile>> = flow {
        val currentUser = authDataSource.getCurrentUser()
        if (currentUser == null) {
            emit(Result.failure(Exception("Pengguna belum login.")))
            return@flow
        }

        try {
            val userDoc = userDataSource.getUserDocument(currentUser.uid)
            if (userDoc != null && userDoc.exists()) {
                val userProfile = UserProfile(
                    uid = currentUser.uid,
                    nama = userDoc.getString("nama") ?: "",
                    nim = userDoc.getString("nim") ?: "",
                    email = userDoc.getString("email") ?: currentUser.email ?: "",
                    jurusan = userDoc.getString("jurusan") ?: "",
                    fotoProfil = userDoc.getString("fotoProfil") ?: currentUser.photoUrl?.toString() ?: "",
                    kontak = userDoc.getString("kontak") ?: "",
                    alamat = userDoc.getString("alamat") ?: "",
                    jenisKelamin = userDoc.getString("jenisKelamin") ?: "",
                    activeRole = userDoc.getString("activeRole") ?: "Pengguna Umum",
                )
                emit(Result.success(userProfile))
            } else {
                Log.w(
                    "AuthRepository",
                    "Dokumen profil pengguna tidak ditemukan di Firestore untuk UID: ${currentUser.uid}"
                )
                emit(Result.failure(Exception("Profil pengguna tidak ditemukan.")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception("Gagal memuat profil: ${e.message}")))
        }
    }.flowOn(Dispatchers.IO)

    fun uploadProfileImageToServer(imageFilePart: MultipartBody.Part): Flow<Result<String>> = flow {
        try {
            val response = apiService.uploadProfileImage(imageFilePart)

            if (response.status) {
                val filePath: String? = response.data
                if (filePath != null) {
                    emit(Result.success(filePath))
                } else {
                    emit(Result.failure(Exception(response.message)))
                }
            } else {
                emit(Result.failure(Exception(response.message)))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception("Tidak dapat terhubung ke server atau terjadi kesalahan: ${e.message}")))
        }
    }.flowOn(Dispatchers.IO)

    fun updateUserProfilePhotoPath(userId: String, newPhotoPath: String): Flow<Result<Unit>> = flow {
        try {
            userDataSource.updateUserProfileField(userId, "fotoProfil", newPhotoPath)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(Exception("Gagal menyimpan path foto ke profil: ${e.message}")))
        }
    }.flowOn(Dispatchers.IO)
}