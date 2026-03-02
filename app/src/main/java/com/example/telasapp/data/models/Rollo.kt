package com.example.telasapp.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Rollo(
    // Campos base de la tabla 'rollos'
    val id: Int? = null,
    @SerialName("tipo_tela") val tipo_tela: String,
    val color: String? = null,
    val codigo: String,

    @SerialName("metros_por_rollo") val metros_por_rollo: Double,
    @SerialName("cantidad_rollos") val cantidad_rollos: Int,

    @SerialName("metros_totales") val metros_totales: Double? = null,
    @SerialName("metros_restantes") val metros_restantes: Double? = null,

    @SerialName("fecha_compra") val fecha_compra: String, // Recibido como String de MySQL
    val proveedor: String? = null,
    @SerialName("registrado_por") val registrado_por: String? = null,

    val estado: String? = "Disponible",
    val precio: Double,
    @SerialName("imagen_url") val imagen_url: String? = null,

    // Campos CALCULADOS que envías en el GET /rollos
    @SerialName("rollos_disponibles") val rollos_disponibles: Int? = null,
    @SerialName("metros_reales_restantes") val metros_reales_restantes: Double? = null,

    // Para cuando consultes el detalle (GET /rollos/:id)
    @SerialName("detalles_rollos") val detalles_rollos: List<DetalleRollo>? = emptyList()
)

@Serializable
data class DetalleRollo(
    val id: Int,
    @SerialName("rollo_padre_id") val rollo_padre_id: Int,
    @SerialName("numero_rollo") val numero_rollo: Int,
    @SerialName("metros_iniciales") val metros_iniciales: Double,
    @SerialName("metros_restantes") val metros_restantes: Double,
    val estado: String
)