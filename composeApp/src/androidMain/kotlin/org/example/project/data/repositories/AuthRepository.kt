package org.example.project.data.repositories

import android.content.Context
import android.provider.Settings
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.example.project.data.remote.AuthDataSource
import org.example.project.data.remote.FcmDataSource
import org.example.project.data.remote.UserDataSource
import org.example.project.ui.viewmodel.AuthViewModel
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val dataSource: AuthDataSource,
    private val userDataSource: UserDataSource,
    private val fcmDataSource: FcmDataSource,
    @ApplicationContext private val context: Context
) {
    private val kodeJurusanMap = mapOf(
        "tif" to "Teknik Komputer dan Informatika",
        "ksy" to "Akuntansi",
        "kpn" to "Akuntansi"
    )

    fun getJurusanFromEmail(email: String): String {
        val namaEmail = email.substringBefore("@")
        val parts = namaEmail.split(".")
        val kodeMentah = parts.getOrNull(2)
        val kode = kodeMentah?.takeWhile { it.isLetter() }

        return kodeJurusanMap[kode?.lowercase()] ?: "Jurusan Tidak Dikenal"
    }

    fun getInitialUserStatus(): Flow<AuthViewModel.AuthState> = flow {
        val currentUser = dataSource.getCurrentUser()
        if (currentUser == null) {
            emit(AuthViewModel.AuthState.Idle)
            return@flow
        }

        val idToken = try {
            dataSource.getCurrentUserIdToken()
        } catch (e: Exception) {
            emit(AuthViewModel.AuthState.Error("Gagal mengambil token: ${e.message}"))
            return@flow
        }

        if (currentUser.email?.endsWith("@polban.ac.id") != true) {
            dataSource.signOut()
            emit(AuthViewModel.AuthState.Error("Sesi tidak valid, hanya email @polban.ac.id yang diperbolehkan."))
            return@flow
        }

        try {
            val userDoc = userDataSource.getUserDocument(currentUser.uid)
            if (userDoc != null && userDoc.exists()) {
                var roles = userDoc.get("roles") as? List<String> ?: emptyList()
                if (roles.isEmpty()) {
                    roles = listOf("Pengguna Umum")
                } else if ("Pengguna Umum" !in roles) {
                    roles = (roles + "Pengguna Umum").distinct()
                }

                val activeRole = userDoc.getString("activeRole")
                if (activeRole != null) {
                    emit(AuthViewModel.AuthState.Success(activeRole))
                } else {
                    emit(AuthViewModel.AuthState.RoleSelectionRequired(roles))
                }
            } else {
                dataSource.signOut()
                emit(AuthViewModel.AuthState.Error("Data pengguna tidak ditemukan di server. Silakan login ulang."))
            }
        } catch (e: Exception) {
            emit(AuthViewModel.AuthState.Error("Gagal memeriksa status pengguna: ${e.message}"))
        }
    }

    fun signInWithGoogle(context: Context, webClientId: String): Flow<AuthViewModel.AuthState> = flow {
        emit(AuthViewModel.AuthState.Loading)
        try {
            val idToken = dataSource.getGoogleIdToken(context, webClientId)
            if (idToken == null) {
                emit(AuthViewModel.AuthState.Error("Gagal mendapatkan ID Token Google."))
                return@flow
            }

            val firebaseUser = dataSource.signInWithGoogleCredential(idToken)
            if (firebaseUser == null) {
                emit(AuthViewModel.AuthState.Error("Login dengan kredensial Google gagal."))
                return@flow
            }

            if (firebaseUser.email?.endsWith("@polban.ac.id") != true) {
                dataSource.signOut()
                emit(AuthViewModel.AuthState.Error("Hanya pengguna dengan akun email Polban (@polban.ac.id) yang dapat login"))
                return@flow
            }

            val userDoc = userDataSource.getUserDocument(firebaseUser.uid)
            if (userDoc != null && userDoc.exists()) {
                var roles = userDoc.get("roles") as? List<String> ?: emptyList()
                if ("Pengguna Umum" !in roles) {
                    roles = (roles + "Pengguna Umum").distinct()
                }
                val activeRole = userDoc.getString("activeRole")

                if (activeRole != null) {
                    emit(AuthViewModel.AuthState.Success(activeRole))
                } else {
                    if (roles.size > 1) {
                        emit(AuthViewModel.AuthState.RoleSelectionRequired(roles))
                    } else {
                        val roleToSet = roles.firstOrNull() ?: "Pengguna Umum"
                        userDataSource.updateUserActiveRole(firebaseUser.uid, roleToSet)
                        emit(AuthViewModel.AuthState.Success(roleToSet))
                    }
                }
            } else {
                val pendingUserDoc = userDataSource.getPendingUserByEmail(firebaseUser.email!!)
                if (pendingUserDoc == null || !pendingUserDoc.exists()) {
                    dataSource.signOut()
                    emit(AuthViewModel.AuthState.Error("Akun Anda tidak terdaftar dalam sistem. Hubungi administrator aplikasi."))
                    return@flow
                }

                val nama = pendingUserDoc.getString("nama")
                val nim = pendingUserDoc.getString("nim")

                if (nama.isNullOrEmpty() || nim.isNullOrEmpty()) {
                    dataSource.signOut()
                    emit(AuthViewModel.AuthState.Error("Data registrasi awal (Nama/NIM) tidak lengkap."))
                    return@flow
                }

                val fcmToken = fcmDataSource.getFcmToken() ?: ""

                val roleDefinitions = userDataSource.getRoleDefinitions()
                val assignedRoles = mutableSetOf("Pengguna Umum")
                val userEmailLower = firebaseUser.email!!.trim().lowercase()

                roleDefinitions.forEach { roleDoc ->
                    val emailsInRole = roleDoc.get("emails") as? List<*>
                    if (emailsInRole?.any { (it as? String)?.trim()?.lowercase() == userEmailLower } == true) {
                        assignedRoles.add(roleDoc.id)
                    }
                }
                val rolesList = assignedRoles.toList()

                emit(
                    AuthViewModel.AuthState.FormRequired(
                        uid = firebaseUser.uid,
                        nama = nama,
                        nim = nim,
                        user = firebaseUser,
                        kontak = null,
                        jenisKelamin = null,
                        roles = rolesList,
                        fcmToken = fcmToken,
                    )
                )
            }
        } catch (e: Exception) {
            dataSource.signOut()
            emit(AuthViewModel.AuthState.Error("Login gagal: ${e.message ?: "Terjadi kesalahan tidak diketahui"}"))
        }
    }

    fun saveNewUserWithDetails(
        uid: String,
        nama: String,
        nim: String,
        user: FirebaseUser,
        kontak: String,
        alamat: String,
        jenisKelamin: String,
        roles: List<String>,
        fcmToken: String
    ): Flow<AuthViewModel.AuthState> = flow {
        emit(AuthViewModel.AuthState.Loading)
        try {
            val activeRoleToSave: String? = if (roles.size == 1) {
                roles.first()
            } else {
                null
            }

            val userData = mapOf(
                "nama" to nama,
                "nim" to nim,
                "jurusan" to getJurusanFromEmail(user.email ?: ""),
                "email" to user.email,
                "fotoProfil" to user.photoUrl?.toString().orEmpty(),
                "kontak" to kontak,
                "alamat" to alamat,
                "jenisKelamin" to jenisKelamin,
                "roles" to roles,
                "activeRole" to activeRoleToSave,
                "lastLogin" to FieldValue.serverTimestamp(),
                "createdAt" to FieldValue.serverTimestamp()
            )
            userDataSource.saveNewUser(uid, userData)

            val deviceId = getDeviceId(context)

            if (fcmToken.isNotEmpty()) {
                val deviceData = mapOf(
                    "fcmToken" to fcmToken,
                    "timestamp" to FieldValue.serverTimestamp(),
                    "platform" to "android",
                    "isActive" to true,
                )
                userDataSource.saveDeviceToken(uid, deviceId, deviceData)
                fcmDataSource.saveFcmToken(fcmToken)
            }

            if (roles.size > 1) {
                emit(AuthViewModel.AuthState.RoleSelectionRequired(roles))
            } else {
                emit(AuthViewModel.AuthState.Success(activeRoleToSave!!))
            }
        } catch (e: Exception) {
            emit(AuthViewModel.AuthState.Error("Gagal menyimpan data pengguna: ${e.message}"))
        }
    }

    private fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun selectActiveRole(role: String): Flow<AuthViewModel.AuthState> = flow {
        emit(AuthViewModel.AuthState.Loading)
        val currentUser = dataSource.getCurrentUser()
        if (currentUser == null) {
            emit(AuthViewModel.AuthState.Error("Sesi pengguna tidak valid."))
            return@flow
        }
        try {
            userDataSource.updateUserActiveRole(currentUser.uid, role)
            emit(AuthViewModel.AuthState.Success(role))
        } catch (e: Exception) {
            emit(AuthViewModel.AuthState.Error("Gagal memperbarui peran: ${e.message}"))
        }
    }

    suspend fun logoutCurrentAccount() {
        val userId = dataSource.getCurrentUser()?.uid
        val deviceId = getDeviceId(context)
        try {
            if (userId != null) {
                userDataSource.clearUserActiveRole(userId, deviceId)
            } else {
            }
        } catch (e: Exception) {
            throw e
        } finally {
            dataSource.signOut()
        }
    }
}