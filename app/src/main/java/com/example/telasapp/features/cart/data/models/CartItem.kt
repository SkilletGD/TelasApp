package com.example.telasapp.features.cart.data.models

import java.util.UUID.randomUUID

data class CartItem(
    val id: String = randomUUID().toString(), // ID único para el carrito
    val rolloId: Int,
    val tipoTela: String,
    val color: String,
    val metros: Double,
    val vendedor: String,
    val cliente: String? = null
)