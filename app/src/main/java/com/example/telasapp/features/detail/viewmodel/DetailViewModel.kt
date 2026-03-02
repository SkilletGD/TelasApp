package com.example.telasapp.features.detail.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telasapp.data.models.Rollo
import com.example.telasapp.data.network.ApiService
import com.example.telasapp.features.export.ExportService
import com.example.telasapp.features.export.LabelGenerator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val _eventos = MutableSharedFlow<String>()
    val eventos = _eventos.asSharedFlow()

    private val _rollo = MutableStateFlow<Rollo?>(null)
    val rollo: StateFlow<Rollo?> = _rollo

    // NUEVO: Estado para saber si estamos cargando o guardando
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Cargar el rollo con sus detalles
    fun cargarRollo(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // El ApiService ahora trae el Rollo con su lista detalles_rollos
                val resultado = ApiService.obtenerRolloPorId(id)
                _rollo.value = resultado
            } catch (e: Exception) {
                _eventos.emit("❌ Error al cargar datos: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarRollo(rollo: Rollo, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Al actualizar un Lote, el servidor ahora valida precio y código
                val exito = ApiService.actualizarRollo(rollo)
                if (exito) {
                    _eventos.emit("✅ Lote '${rollo.codigo}' actualizado")
                    onSuccess()
                } else {
                    _eventos.emit("❌ El servidor no permitió la actualización")
                }
            } catch (e: Exception) {
                _eventos.emit("❌ Error: No se pudo conectar con el servidor")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarRollo(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val exito = ApiService.eliminarRollo(id)
                if (exito) {
                    _eventos.emit("🗑️ Lote y rollos asociados eliminados")
                    onSuccess()
                }
            } catch (e: Exception) {
                _eventos.emit("❌ Error al eliminar: Verifique su conexión")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- SERVICIOS DE EXPORTACIÓN ---
    // Nota: Asegúrate de que LabelGenerator acepte el nuevo modelo Rollo con metros_reales_restantes
    fun imprimirEtiqueta(context: Context, rollo: Rollo) {
        viewModelScope.launch {
            try {
                val generator = LabelGenerator(context)
                val service = ExportService(context)
                val pdfFile = generator.generarPDFEtiqueta(rollo)
                service.imprimirPDF(pdfFile)
            } catch (e: Exception) {
                _eventos.emit("❌ Error al generar impresión")
            }
        }
    }

    fun compartirEtiqueta(context: Context, rollo: Rollo) {
        viewModelScope.launch {
            try {
                val generator = LabelGenerator(context)
                val service = ExportService(context)
                val pdfFile = generator.generarPDFEtiqueta(rollo)
                service.compartirArchivo(pdfFile)
            } catch (e: Exception) {
                _eventos.emit("❌ Error al compartir PDF")
            }
        }
    }
}