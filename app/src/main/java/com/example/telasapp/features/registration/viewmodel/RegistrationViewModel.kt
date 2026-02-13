package com.example.telasapp.features.registration.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telasapp.data.models.Rollo
import com.example.telasapp.data.network.ApiService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {

    private val _eventos = MutableSharedFlow<String>()
    val eventos = _eventos.asSharedFlow()

    fun agregarRollo(rollo: Rollo, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                ApiService.crearRollo(rollo)
                _eventos.emit("✅ Rollo registrado con éxito")
                onSuccess()
            } catch (e: Exception) {
                _eventos.emit("❌ Error: ${e.message}")
            }
        }
    }
}