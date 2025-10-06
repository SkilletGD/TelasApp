package com.example.telasapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telasapp.data.models.Rollo
import com.example.telasapp.data.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventarioViewModel : ViewModel() {

    private val _rollos = MutableStateFlow<List<Rollo>>(emptyList())
    val rollos: StateFlow<List<Rollo>> = _rollos

    fun cargarRollos() {
        viewModelScope.launch {
            try {
                _rollos.value = ApiService.obtenerRollos()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun agregarRollo(rollo: Rollo) {
        viewModelScope.launch {
            try {
                val nuevoRollo = ApiService.crearRollo(rollo)
                _rollos.value = _rollos.value + nuevoRollo
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
