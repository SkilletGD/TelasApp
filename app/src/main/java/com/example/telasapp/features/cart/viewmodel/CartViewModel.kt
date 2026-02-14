package com.example.telasapp.features.cart.viewmodel

import androidx.lifecycle.ViewModel
import com.example.telasapp.features.cart.data.models.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items = _items.asStateFlow()

    fun agregar(item: CartItem) {
        _items.value = _items.value + item
    }

    fun eliminar(item: CartItem) {
        _items.value = _items.value.filter { it.id != item.id }
    }

    fun limpiar() {
        _items.value = emptyList()
    }
}