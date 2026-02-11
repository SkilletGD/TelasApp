package com.example.telasapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Rollo(
    val id: Int? = null,
    val tipo_tela: String,
    val color: String,
    val codigo: String,
    val cantidad_total: String,
    val cantidad_restante: String,
    val precio_por_metro: Double = 0.0,        // NUEVO - Precio por metro lineal
    val precio_rollo_completo: Double = 0.0,   // NUEVO - Precio total del rollo
    val fecha_compra: String? = null,
    val proveedor: String? = null,
    val registrado_por: String,
    val estado: String = "Disponible"
)