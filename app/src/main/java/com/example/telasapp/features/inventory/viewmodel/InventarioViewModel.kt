package com.example.telasapp.features.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telasapp.data.models.Rollo
import com.example.telasapp.data.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import android.util.Log
class InventarioViewModel : ViewModel() {

    // 1. Estados de la Lista
    private val _rollos = MutableStateFlow<List<Rollo>>(emptyList())
    val rollos: StateFlow<List<Rollo>> = _rollos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // 2. Canal para mensajes (Snackbar)
    private val _eventos = MutableSharedFlow<String>()
    val eventos = _eventos.asSharedFlow()

    // 3. Carga de datos (Responsabilidad principal)
    fun cargarRollos() {
        viewModelScope.launch {
            // 1. IMPORTANTE: No limpies _rollos.value aquí.
            // Si ya hay datos, deja que se vean mientras el spinner de arriba gira.

            _isLoading.value = true
            _errorMessage.value = null // Limpiamos el error para que la "ErrorCard" desaparezca

            try {
                // 2. Llamada a la API
                val resultado = ApiService.obtenerRollos()

                // 3. Actualizamos los datos
                _rollos.value = resultado
                Log.d("API_SUCCESS", "Se cargaron ${resultado.size} rollos")

            } catch (e: Exception) {
                val errorReal = e.localizedMessage ?: e.message ?: "Error desconocido"
                Log.e("API_ERROR", "Detalle del fallo: $errorReal", e)

                // 4. Si falla, mostramos el error pero NO borramos los rollos que ya teníamos
                // así el usuario al menos ve la última información conocida.
                _errorMessage.value = "Fallo: $errorReal"
                _eventos.emit("❌ Error: $errorReal")
            } finally {
                // 5. Esto detiene la animación del PullToRefresh en la UI
                _isLoading.value = false
            }
        }
    }

    // Nota: El borrado se queda aquí solo si el botón de eliminar
    // está en la lista principal. Si está en el detalle, se mueve allá.
    fun eliminarRollo(rolloId: Int) {
        viewModelScope.launch {
            try {
                val fueEliminado = ApiService.eliminarRollo(rolloId)
                if (fueEliminado) {
                    _rollos.value = _rollos.value.filter { it.id != rolloId }
                    _eventos.emit("✅ Rollo eliminado correctamente")
                }
            } catch (e: Exception) {
                _eventos.emit("❌ Error al eliminar el rollo")
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}