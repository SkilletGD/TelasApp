package com.example.telasapp.features.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telasapp.features.cart.data.models.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CartViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items = _items.asStateFlow()

    val totalMetros: StateFlow<Double> = _items
        .map { lista -> lista.sumOf { it.metros } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Se detiene 5s después de cerrar la pantalla
            initialValue = 0.0
        )
    fun agregar(nuevoItem: CartItem) {
        val listaActual = _items.value.toMutableList()

        // REGLA DE NEGOCIO: Si el rollo físico ya está en el carrito,
        // podrías sumar los metros o simplemente reemplazarlo.
        // Aquí optamos por reemplazar para evitar duplicados del mismo ID físico.
        val indiceExistente = listaActual.indexOfFirst { it.rolloId == nuevoItem.rolloId }

        if (indiceExistente != -1) {
            listaActual[indiceExistente] = nuevoItem
        } else {
            listaActual.add(nuevoItem)
        }

        _items.value = listaActual
    }

    fun eliminar(item: CartItem) {
        _items.value = _items.value.filter { it.id != item.id }
    }

    fun limpiar() {
        _items.value = emptyList()
    }

    // Útil para verificar si el carrito tiene contenido antes de intentar un checkout
    fun estaVacio(): Boolean = _items.value.isEmpty()
}