package com.example.telasapp.features.auth.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telasapp.features.auth.data.repository.AuthRepository
import kotlinx.coroutines.launch

// Ruta: com.example.telasapp.features.auth.viewmodel.AuthConfirmViewModel
class AuthConfirmViewModel(private val repository: AuthRepository) : ViewModel() {
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun confirmar(email: String, onConfirm: () -> Unit) {
        if (password.isBlank()) {
            error = "Ingresa la contraseña"
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null
            repository.verificarPassword(email, password)
                .onSuccess {
                    password = "" // Limpiar
                    onConfirm()
                }
                .onFailure {
                    error = "Contraseña incorrecta"
                }
            isLoading = false
        }
    }

    fun reset() {
        password = ""
        error = null
        isLoading = false
    }
}