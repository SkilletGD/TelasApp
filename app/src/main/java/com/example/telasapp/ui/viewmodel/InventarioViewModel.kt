package com.example.telasapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telasapp.data.models.Rollo
import com.example.telasapp.data.models.Venta
import com.example.telasapp.data.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventarioViewModel : ViewModel() {

    private val _rollos = MutableStateFlow<List<Rollo>>(emptyList())
    val rollos: StateFlow<List<Rollo>> = _rollos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun cargarRollos() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _rollos.value = ApiService.obtenerRollos()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar rollos: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun agregarRollo(rollo: Rollo) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val nuevoRollo = ApiService.crearRollo(rollo)
                _rollos.value = _rollos.value + nuevoRollo
            } catch (e: Exception) {
                _errorMessage.value = "Error al crear rollo: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarRollo(rollo: Rollo) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                ApiService.actualizarRollo(rollo)
                _rollos.value = _rollos.value.map { if (it.id == rollo.id) rollo else it }
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar rollo: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registrarVenta(rolloId: Int, metrosVendidos: Double, vendedor: String, cliente: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val venta = Venta(
                    rollo_id = rolloId,
                    cantidad_vendida = metrosVendidos,
                    vendedor = vendedor,
                    cliente = cliente
                )

                ApiService.registrarVenta(venta)

                // Recargar los rollos para actualizar estados
                cargarRollos()

            } catch (e: Exception) {
                _errorMessage.value = "Error al registrar venta: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función para limpiar errores
    fun clearError() {
        _errorMessage.value = null
    }
}