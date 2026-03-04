package com.example.telasapp.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telasapp.data.models.User
import com.example.telasapp.data.models.UserRole
import com.example.telasapp.features.auth.data.repository.AuthRepository // IMPORTA TU REPOSITORIO
import com.example.telasapp.data.preferences.TokenManager // IMPORTA TU TOKEN MANAGER
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Llena todos los campos")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            repository.login(email, pass)
                .onSuccess { response ->
                    val user = User(email = response.email ?: email, role = UserRole.VENDEDOR)
                    _authState.value = AuthState.Success(user)
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Error al conectar")
                }
        }
    }

    // Nota: El TokenManager debería ser inyectado o pasado desde el repositorio,
    // pero si lo pasas por aquí, asegúrate de que el import sea correcto.
    fun logout(tokenManager: TokenManager) {
        viewModelScope.launch {
            tokenManager.clearSession()
            _authState.value = AuthState.Idle
        }
    }
}