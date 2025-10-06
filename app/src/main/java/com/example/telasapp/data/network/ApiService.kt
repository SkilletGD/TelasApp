package com.example.telasapp.data.network

import com.example.telasapp.data.models.Rollo
import com.example.telasapp.data.models.Venta
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object ApiService {
    private val client = ApiClient.client
    private const val BASE_URL = ApiClient.BASE_URL

    suspend fun obtenerRollos(): List<Rollo> =
        client.get("$BASE_URL/inventory").body()

    suspend fun crearRollo(rollo: Rollo): Rollo =
        client.post("$BASE_URL/inventory") {
            contentType(ContentType.Application.Json)
            setBody(rollo)
        }.body()

    suspend fun registrarVenta(venta: Venta): Venta =
        client.post("$BASE_URL/sales") {
            contentType(ContentType.Application.Json)
            setBody(venta)
        }.body()
}
