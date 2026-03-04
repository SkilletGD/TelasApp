package com.example.telasapp.features.cart.data.models


data class CartItem(
    val id: Int = System.currentTimeMillis().toInt(),
    val rolloId: Int,          // ID del rollo físico individual (detalle_rollos)
    val loteCodigo: String,    // NUEVO: Para mostrar "Lote: ABC-123" en el carrito
    val tipoTela: String,
    val color: String?,        // Cambiado a nullable para evitar errores si no tiene color
    val metros: Double,
    val vendedor: String,
    val cliente: String? = null
)