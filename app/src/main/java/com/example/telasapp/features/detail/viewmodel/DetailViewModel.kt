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

    fun actualizarRollo(rollo: Rollo, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                ApiService.actualizarRollo(rollo)
                _eventos.emit("✅ Cambios guardados")
                onSuccess()
            } catch (e: Exception) {
                _eventos.emit("❌ Error al guardar")
            }
        }
    }

    fun eliminarRollo(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val exito = ApiService.eliminarRollo(id)
                if (exito) {
                    _eventos.emit("🗑️ Rollo eliminado")
                    onSuccess()
                }
            } catch (e: Exception) {
                _eventos.emit("❌ No se pudo eliminar")
            }
        }
    }

    fun imprimirEtiqueta(context: Context, rollo: Rollo) {
        val generator = LabelGenerator(context)
        val service = ExportService(context)
        val pdfFile = generator.generarPDFEtiqueta(rollo)
        service.imprimirPDF(pdfFile)
    }

    fun compartirEtiqueta(context: Context, rollo: Rollo) {
        val generator = LabelGenerator(context)
        val service = ExportService(context)
        val pdfFile = generator.generarPDFEtiqueta(rollo)
        service.compartirArchivo(pdfFile)
    }

    // Nueva función para cargar el rollo
    fun cargarRollo(id: Int) {
        viewModelScope.launch {
            try {
                // Llamamos a la API para traer el rollo actualizado
                val resultado = ApiService.obtenerRolloPorId(id)
                _rollo.value = resultado
            } catch (e: Exception) {
                _eventos.emit("❌ Error al cargar datos")
            }
        }
    }
}