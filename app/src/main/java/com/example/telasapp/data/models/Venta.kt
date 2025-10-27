package com.example.telasapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Venta(
    val id: Int? = null,
    val rollo_id: Int,
    val cantidad_vendida: Double, // ← CORREGIDO: debe coincidir con tu API
    val vendedor: String,
    val cliente: String? = null
)