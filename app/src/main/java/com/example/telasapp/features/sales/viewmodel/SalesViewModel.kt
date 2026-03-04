package com.example.telasapp.features.sales.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telasapp.data.models.Rollo
import com.example.telasapp.data.models.Venta
import com.example.telasapp.data.network.ApiService
import com.example.telasapp.features.cart.data.models.CartItem
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

    private val _eventos = MutableSharedFlow<String>()
    val eventos = _eventos.asSharedFlow()

    fun cargarRolloParaVenta(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _rolloActual.value = null // <-- CRÍTICO: Limpiar antes de pedir el nuevo
            try {
                val resultado = ApiService.obtenerRolloPorId(id)
                _rolloActual.value = resultado
            } catch (e: Exception) {
                _errorMessage.value = "Error al obtener datos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarVentas() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null // Limpiamos error previo al reintentar
            try {
                _ventas.value = ApiService.obtenerVentas()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar historial: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registrarVenta(rolloId: Int, metros: Double, vendedor: String, cliente: String?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // LOG DE DEPURACIÓN: Revisa tu Logcat para ver qué ID está llegando aquí
                println("DEBUG_VENTA: Enviando venta para el ID: $rolloId")

                val venta = Venta(
                    rollo_id = rolloId, // <--- ASEGÚRATE QUE SEA EL PARÁMETRO
                    metros_vendidos = metros,
                    vendedor = vendedor,
                    cliente = cliente
                )

                // Llamada a la API
                ApiService.registrarVenta(venta)

                _eventos.emit("✅ Venta registrada")
                onSuccess()
            } catch (e: Exception) {
                _eventos.emit("❌ Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registrarVentaMasiva(items: List<CartItem>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                items.forEach { item ->
                    // --- CORRECCIÓN CLAVE: Usamos metros_vendidos ---
                    val venta = Venta(
                        rollo_id = item.rolloId,
                        metros_vendidos = item.metros,
                        vendedor = item.vendedor,
                        cliente = item.cliente
                    )
                    ApiService.registrarVenta(venta)
                }

                _eventos.emit("✅ Pedido completo registrado (${items.size} productos)")
                onSuccess()
            } catch (e: Exception) {
                _eventos.emit("❌ Error en el pedido: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _errorMessage.value = null }
}