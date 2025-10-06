package com.example.telasapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Venta(
    val id: Int? = null,
    val rollo_id: Int,
    val metros_vendidos: Double,
    val vendedor: String,
    val cliente: String? = null
)