package org.example.project.ui.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _availableRoles = MutableStateFlow<List<String>>(emptyList())
    val availableRoles: StateFlow<List<String>> = _availableRoles

    sealed interface AuthState {
        object Idle : AuthState
        object Loading : AuthState
        data class Success(val activeRole: String) : AuthState
        data class Error(val message: String) : AuthState
        data class FormRequired(
            val uid: String,
            val nama: String,
            val nim: String,
            val user: FirebaseUser,
            val kontak: String?,
            val jenisKelamin: String?,
            val selectedRole: String?,
            val roles: List<String>,
            val fcmToken: String,
        ) : AuthState

        data class RoleSelectionRequired(val roles: List<String>) : AuthState
    }

    fun startGoogleSignIn(context: Context, webClientId: String, onLoadingChanged: (Boolean) -> Unit) {
        onLoadingChanged(true)
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val authToken = getGoogleIdToken(context, webClientId) ?: run {
                    _authState.value = AuthState.Error("ID Token null")
                    onLoadingChanged(false)
                    return@launch
                }

                signInWithGoogleIDToken(authToken)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Login gagal: ${e.message}")
                onLoadingChanged(false)
            }
        }
    }

    private suspend fun getGoogleIdToken(context: Context, webClientId: String): String? {
        try {
            val credentialManager = CredentialManager.create(context)
            val request = GetCredentialRequest(
                credentialOptions = listOf(
                    GetGoogleIdOption.Builder()
                        .setServerClientId(webClientId)
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
            )

            val result = credentialManager.getCredential(context, request)
            return (result.credential as? GoogleIdTokenCredential)?.idToken
        } catch (e: GetCredentialException) {
            throw e
        }
    }

    private fun signInWithGoogleIDToken(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user?.email?.endsWith("polban.ac.id") != true) {
                    auth.signOut()
                    _authState.value = AuthState.Error("Hanya email @polban.ac.id yang diperbolehkan")
                } else {
                    db.collection("users").document(user.uid).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                handleExistingUser(document)
                            } else {
                                checkPendingUser(user)
                            }
                        }
                        .addOnFailureListener {
                            _authState.value = AuthState.Error("Gagal memeriksa status pengguna: ${it.message}")
                        }
                }
            } else {
                _authState.value = AuthState.Error("Login gagal: ${task.exception?.message}")
            }
        }
    }

    private fun handleExistingUser(doc: com.google.firebase.firestore.DocumentSnapshot) {
        val currentRole = doc.getString("role") ?: "User"
        val roles = doc.get("roles") as? List<String> ?: listOf("User")
        _availableRoles.value = roles

        if (roles.size > 1) {
            _authState.value = AuthState.RoleSelectionRequired(roles)
        } else {
            _authState.value = AuthState.Success(currentRole)
        }
    }

    private fun checkPendingUser(user: FirebaseUser) {
        db.collection("users_pending").whereEqualTo("email", user.email).get()
            .addOnSuccessListener { pendingDocs ->
                val doc = pendingDocs.documents.firstOrNull()
                val nama = doc?.getString("nama")
                val nim = doc?.getString("nim")
                if (nama.isNullOrEmpty() || nim.isNullOrEmpty()) {
                    _authState.value = AuthState.Error("Data pengguna tidak lengkap: Nama atau NIM kosong")
                    return@addOnSuccessListener
                }
                Log.d("AuthViewModel", "User found in pending collection: $nama, $nim")
                checkRolesAndSave(user, nama, nim)
            }
            .addOnFailureListener {
                Log.e("AuthViewModel", "Failed to check pending user: ${it.message}")
                _authState.value = AuthState.Error("Gagal memeriksa status pengguna: ${it.message}")
            }
    }

    private fun checkRolesAndSave(
        user: FirebaseUser,
        nama: String,
        nim: String,
    ) {
        val email = user.email?.trim()?.lowercase() ?: ""
        val uid = user.uid

        viewModelScope.launch {
            try {
                val fcmToken = getFcmTokenSuspend() ?: throw Exception("Gagal mengambil FCM token")
                val snapshot = db.collection("roles").get().await()
                val roles = mutableSetOf("User")
                for (doc in snapshot) {
                    val emails = doc.get("emails") as? List<*>
                    if (emails?.contains(email) == true) {
                        roles.add(doc.id)
                    }
                }
                val rolesList = roles.toList()
                _availableRoles.value = rolesList
                _authState.value = AuthState.FormRequired(
                    uid = uid,
                    nama = nama,
                    nim = nim,
                    user = user,
                    kontak = null,
                    jenisKelamin = null,
                    selectedRole = null,
                    roles = rolesList,
                    fcmToken = fcmToken
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Login gagal: ${e.message}")
            }
        }
    }

    fun saveUserToFirestore(
        uid: String,
        nama: String,
        nim: String,
        user: FirebaseUser,
        kontak: String,
        alamat: String,
        jenisKelamin: String,
        selectedRole: String,
        roles: List<String>,
        fcmToken: String
    ) {
        val userRef = db.collection("users").document(uid)

        val userData = mapOf(
            "nama" to nama,
            "nim" to nim,
            "jurusan" to getJurusan(user.email ?: ""),
            "email" to user.email,
            "fotoProfil" to user.photoUrl?.toString().orEmpty(),
            "kontak" to kontak,
            "kontak" to alamat,
            "jenisKelamin" to jenisKelamin,
            "roles" to roles,
            "activeRole" to selectedRole,
            "lastLogin" to FieldValue.serverTimestamp()
        )

        userRef.set(userData, SetOptions.merge())
            .addOnSuccessListener {
                val deviceData = mapOf(
                    "fcmToken" to fcmToken,
                    "timestamp" to FieldValue.serverTimestamp()
                )
                userRef.collection("devices").document(uid).set(deviceData, SetOptions.merge())
                    .addOnSuccessListener {
                        if (roles.size > 1) {
                            _availableRoles.value = roles
                            _authState.value = AuthState.RoleSelectionRequired(roles)
                        } else {
                            _authState.value = AuthState.Success(selectedRole)
                        }
                    }
                    .addOnFailureListener { e ->
                        _authState.value = AuthState.Error("Gagal menyimpan data perangkat: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error("Gagal menyimpan user: ${e.message}")
            }
    }

    private fun getJurusan(email: String): String {
        val namaEmail = email.substringBefore("@")
        val parts = namaEmail.split(".")
        val kodeMentah = parts.getOrNull(2)
        val kode = kodeMentah?.takeWhile { it.isLetter() }

        return kodeJurusanMap[kode?.lowercase()] ?: "Jurusan Tidak Dikenal"
    }

    private val kodeJurusanMap = mapOf(
        "tif" to "Teknik Komputer dan Informatika",
        "ksy" to "Akuntansi",
        "kpn" to "Akuntansi"
    )

//    fun saveUserLocally(
//        uid: String,
//        kode: String?,
//        nama: String?,
//        email: String?,
//        photoUrl: String?,
//        activeRole: String?,
//        roles: List<String>?,
//        kontak: String?,
//        jnsKelamin: String?,
//        tglLahir: Date,
//        alamat: String?,
//        lastLogin: Long?,
//        fcmToken: String?
//    ) {
//        viewModelScope.launch {
//            val userEntity = UserEntity(
//                uid = uid,
//                kode = kode,
//                nama = nama,
//                email = email,
//                photoUrl = photoUrl,
//                activeRole = activeRole,
//                roles = roles,
//                kontak = kontak,
//                jnsKelamin = jnsKelamin,
//                tglLahir = tglLahir,
//                alamat = alamat,
//                lastLogin = lastLogin,
//                fcmToken = fcmToken
//            )
//            val userRef = db.collection("users").document(uid)
//            val additionalData = mapOf(
//                "kontak" to kontak
//            )
//
//            userRef.set(additionalData, SetOptions.merge())
//                .addOnSuccessListener {
//                    if (roles != null && roles.size > 1) {
//                        _authState.value = AuthState.RoleSelectionRequired(roles)
//                    } else {
//                        _authState.value = AuthState.Success(activeRole ?: "user")
//                    }
//                }
//        }
//    }

    fun selectRole(role: String) {
        val user = auth.currentUser ?: return
        val uid = user.uid

        db.collection("users").document(uid).update("activeRole", role)
            .addOnSuccessListener {
                _authState.value = AuthState.Success(role)
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error("Gagal memperbarui role: ${it.message}")
            }
    }

    fun handleRoleSelection(
        authViewModel: AuthViewModel,
        role: String,
        context: Context,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            val user = authViewModel.auth.currentUser
            if (user == null) {
                Toast.makeText(context, "Sesi pengguna tidak valid, silakan login ulang", Toast.LENGTH_LONG).show()
                clearAvailableRoles()
                auth.signOut()
                _authState.value = AuthState.Idle
                return@launch
            }
            authViewModel.selectRole(role)
            authViewModel.clearAvailableRoles()
        }
    }

    fun clearAvailableRoles() {
        _availableRoles.value = emptyList()
    }

//    fun logout() {
//        auth.signOut()
//        viewModelScope.launch(Dispatchers.IO) {
//            auth.currentUser?.uid?.let { uid ->
//                userDao.deleteUserById(uid)
//            }
//        }
//        _authState.value = AuthState.Idle
//        _availableRoles.value = emptyList()
//    }

    private suspend fun getFcmTokenSuspend(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Failed to get FCM token: ${e.message}")
            null
        }
    }
}