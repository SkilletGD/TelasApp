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
    val fecha_compra: String? = null,
    val proveedor: String? = null,
    val registrado_por: String,
    val estado: String = "Disponible"
)