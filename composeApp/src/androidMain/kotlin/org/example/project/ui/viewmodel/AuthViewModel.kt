package org.example.project.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.example.project.data.repositories.AuthRepository
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _availableRoles = MutableStateFlow<List<String>>(emptyList())
    val availableRoles: StateFlow<List<String>> = _availableRoles.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)

    private val _userRole = MutableStateFlow<String?>(null)

    private val _isAuthChecked = MutableStateFlow(false)
    val isAuthChecked: StateFlow<Boolean> = _isAuthChecked.asStateFlow()

    private val isLoggingOut = AtomicBoolean(false)

    init {
        checkInitialAuthStatus()
    }

    private fun checkInitialAuthStatus() {
        if (isLoggingOut.get()) {
            return
        }

        viewModelScope.launch {
            repository.getInitialUserStatus()
                .catch { e ->
                    if (!isLoggingOut.get()) {
                        _authState.value = AuthState.Error("Gagal memeriksa status awal: ${e.message}")
                    }
                    _isAuthChecked.value = true
                }
                .collect { state ->
                    if (!isLoggingOut.get()) {
                        _authState.value = state
                        when (state) {
                            is AuthState.Success -> {
                                _isLoggedIn.value = true
                                _userRole.value = state.activeRole
                                _availableRoles.value = emptyList()
                            }

                            is AuthState.RoleSelectionRequired -> {
                                _isLoggedIn.value = true
                                _userRole.value = null
                                _availableRoles.value = state.roles
                            }

                            else -> {
                                _isLoggedIn.value = false
                                _userRole.value = null
                            }
                        }
                    }

                    if (state !is AuthState.Loading) {
                        _isAuthChecked.value = true
                    }
                }
        }
    }

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
            val roles: List<String>,
            val fcmToken: String
        ) : AuthState
        data class RoleSelectionRequired(val roles: List<String>) : AuthState
    }

    fun startGoogleSignIn(context: Context, webClientId: String) {
        if (isLoggingOut.get()) {
            _authState.value = AuthState.Error("Proses logout sedang berlangsung.")
            return
        }
        viewModelScope.launch {
            repository.signInWithGoogle(context, webClientId)
                .catch { e ->
                    _authState.value = AuthState.Error("Login gagal: ${e.message ?: "Kesalahan tidak diketahui"}")
                }
                .collect { state ->
                    _authState.value = state
                    if (state is AuthState.RoleSelectionRequired) {
                        _availableRoles.value = state.roles
                    } else if (state is AuthState.Success) {
                        _availableRoles.value = emptyList()
                    }
                }
        }
    }

    fun saveCompletedForm(
        originalFormRequiredState: AuthState.FormRequired,
        kontak: String,
        alamat: String,
        jenisKelamin: String
    ) {
        viewModelScope.launch {
            repository.saveNewUserWithDetails(
                uid = originalFormRequiredState.uid,
                nama = originalFormRequiredState.nama,
                nim = originalFormRequiredState.nim,
                user = originalFormRequiredState.user,
                kontak = kontak,
                alamat = alamat,
                jenisKelamin = jenisKelamin,
                roles = originalFormRequiredState.roles,
                fcmToken = originalFormRequiredState.fcmToken
            )
                .catch { e ->
                    _authState.value = AuthState.Error("Gagal menyimpan data: ${e.message}")
                }
                .collect { newState ->
                    _authState.value = newState
                    if (newState is AuthState.RoleSelectionRequired) {
                        _availableRoles.value = newState.roles
                    } else if (newState is AuthState.Success) {
                        _availableRoles.value = emptyList()
                    }
                }
        }
    }


    fun finalizeRoleSelection(role: String) {
        viewModelScope.launch {
            repository.selectActiveRole(role)
                .catch { e ->
                    _authState.value = AuthState.Error("Gagal memilih peran: ${e.message}")
                }
                .collect { newState ->
                    _authState.value = newState
                    if (newState is AuthState.Success) {
                        clearAvailableRoles()
                    }
                }
        }
    }

    fun clearAvailableRoles() {
        _availableRoles.value = emptyList()
    }

    fun logout(onResult: (Boolean) -> Unit) {
        if (!isLoggingOut.compareAndSet(false, true)) {
            viewModelScope.launch(Dispatchers.Main) {
                onResult(false)
            }
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            var operationSuccess = false
            try {
                repository.logoutCurrentAccount()
                operationSuccess = true
            } catch (e: Exception) {
                operationSuccess = false
            } finally {
                resetLocalAuthState()
                isLoggingOut.set(false)
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    onResult(operationSuccess)
                }
            }
        }
    }

    private fun resetLocalAuthState() {
        _authState.value = AuthState.Idle
        _availableRoles.value = emptyList()
        _userRole.value = null
        _isLoggedIn.value = false
    }
}