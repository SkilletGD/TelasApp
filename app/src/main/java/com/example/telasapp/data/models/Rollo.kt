package com.example.telasapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Rollo(
    val id: Int? = null,
    val tipo_tela: String,
    val color: String,
    val codigo: String,
    val cantidad_total: Double,
    val cantidad_restante: Double,
    val estado: String = "Disponible",
    val fecha_compra: String? = null,
    val proveedor: String? = null
)