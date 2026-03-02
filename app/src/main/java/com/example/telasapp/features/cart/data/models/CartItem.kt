package com.example.telasapp.features.cart.data.models

import java.util.UUID.randomUUID

data class CartItem(
    val id: String = randomUUID().toString(),
    val rolloId: Int,          // ID del rollo físico individual (detalle_rollos)
    val loteCodigo: String,    // NUEVO: Para mostrar "Lote: ABC-123" en el carrito
    val tipoTela: String,
    val color: String?,        // Cambiado a nullable para evitar errores si no tiene color
    val metros: Double,
    val vendedor: String,
    val cliente: String? = null
)