package com.example.telasapp.features.sales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telasapp.data.models.Rollo
import com.example.telasapp.data.models.Venta
import com.example.telasapp.data.network.ApiService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SalesViewModel : ViewModel() {


    private val _rolloActual = MutableStateFlow<Rollo?>(null)
    val rolloActual: StateFlow<Rollo?> = _rolloActual

    private val _ventas = MutableStateFlow<List<Venta>>(emptyList())
    val ventas: StateFlow<List<Venta>> = _ventas

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Canal para mensajes (Snackbars)
    private val _eventos = MutableSharedFlow<String>()
    val eventos = _eventos.asSharedFlow()

    fun cargarRolloParaVenta(id: Int) {
        viewModelScope.launch {
            try {
                val resultado = ApiService.obtenerRolloPorId(id)
                _rolloActual.value = resultado
            } catch (e: Exception) {
                _eventos.emit("❌ Error al obtener datos del rollo")
            }
        }
    }

    fun cargarVentas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _ventas.value = ApiService.obtenerVentas()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar ventas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registrarVenta(rolloId: Int, metros: Double, vendedor: String, cliente: String?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val venta = Venta(rollo_id = rolloId, cantidad_vendida = metros, vendedor = vendedor, cliente = cliente)
                ApiService.registrarVenta(venta)
                _eventos.emit("✅ Venta registrada correctamente")
                onSuccess()
            } catch (e: Exception) {
                _eventos.emit("❌ Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _errorMessage.value = null }
}