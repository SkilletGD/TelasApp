package com.example.telasapp.features.registration.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telasapp.data.models.Rollo
import com.example.telasapp.data.network.ApiService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {

    private val _eventos = MutableSharedFlow<String>()
    val eventos = _eventos.asSharedFlow()

    // Agregamos un estado de carga para deshabilitar el botón mientras se guarda
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun agregarRollo(rollo: Rollo, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // El ApiService ahora devuelve el Rollo creado con sus detalles
                val resultado = ApiService.crearRollo(rollo)

                // Podemos ser más específicos en el mensaje usando los datos que devuelve la API
                _eventos.emit("✅ Lote '${resultado.codigo}' creado con ${resultado.cantidad_rollos} rollos")

                onSuccess()
            } catch (e: Exception) {
                // Manejo de errores más descriptivo
                val errorMsg = e.message ?: "Error desconocido al registrar"
                _eventos.emit("❌ $errorMsg")
            } finally {
                _isLoading.value = false
            }
        }
    }
}