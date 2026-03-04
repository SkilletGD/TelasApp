package com.example.telasapp.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Rollo(
    val id: Int? = null,
    // Les ponemos valores por defecto para que si la API falla, el modelo no explote
    @SerialName("tipo_tela") val tipo_tela: String = "",
    val color: String? = null,
    val codigo: String = "",

    @SerialName("metros_por_rollo") val metros_por_rollo: Double = 0.0,
    @SerialName("cantidad_rollos") val cantidad_rollos: Int = 0,

    @SerialName("metros_totales") val metros_totales: Double? = null,
    @SerialName("metros_restantes") val metros_restantes: Double? = null,

    @SerialName("fecha_compra") val fecha_compra: String = "",
    val proveedor: String? = null,
    @SerialName("registrado_por") val registrado_por: String? = null,

    val estado: String? = "Disponible",
    val precio: Double = 0.0, // <-- Si la API no manda precio, ahora será 0.0 en lugar de crashear
    @SerialName("imagen_url") val imagen_url: String? = null,

    @SerialName("rollos_disponibles") val rollos_disponibles: Int? = null,
    @SerialName("metros_reales_restantes") val metros_reales_restantes: Double? = null,

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