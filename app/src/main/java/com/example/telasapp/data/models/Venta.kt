package com.example.telasapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Venta(
    val id: Int? = null,
    val rollo_id: Int,
    val metros_vendidos: Double, // ← CAMBIADO: ahora coincide con tu API de Node.js
    val precio_unitario: Double? = null, // La API lo calcula, pero puede venir en el GET
    val total_venta: Double? = null,
    val vendedor: String,
    val cliente: String? = null,
    val fecha_venta: String? = null
)