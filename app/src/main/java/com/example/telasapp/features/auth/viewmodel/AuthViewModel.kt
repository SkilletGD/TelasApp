package com.example.telasapp.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telasapp.data.models.User     // Importa el modelo
import com.example.telasapp.data.models.UserRole // Importa el enum
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// El estado vive aquí, fuera de la clase pero en el mismo archivo
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            delay(1000)

            if (email == "admin@telas.com" && pass == "admin123") {
                _authState.value = AuthState.Success(User(email, UserRole.ADMIN))
            } else if (email.contains("@") && pass.length >= 6) {
                _authState.value = AuthState.Success(User(email, UserRole.VENDEDOR))
            } else {
                _authState.value = AuthState.Error("Credenciales inválidas")
            }
        }
    }

    fun logout() {
        _authState.value = AuthState.Idle
    }
}