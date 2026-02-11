package com.example.telasapp.data.network

import com.example.telasapp.data.models.Rollo
import com.example.telasapp.data.models.Venta
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

object ApiService {
    private val client = ApiClient.client
    private const val BASE_URL = ApiClient.BASE_URL

    suspend fun obtenerRollos(): List<Rollo> {
        return client.get("$BASE_URL/rollos").body()
    }

    suspend fun crearRollo(rollo: Rollo): Rollo =
        client.post("$BASE_URL/rollos") {
            contentType(ContentType.Application.Json)
            setBody(rollo)
        }.body()

    suspend fun actualizarRollo(rollo: Rollo): Rollo =
        client.put("$BASE_URL/rollos/${rollo.id}") {
            contentType(ContentType.Application.Json)
            setBody(rollo)
        }.body()

    // En tu ApiService.kt - AGREGA esta función
    suspend fun eliminarRollo(rolloId: Int): Boolean {
        val response: HttpResponse = client.delete("$BASE_URL/rollos/$rolloId")
        return response.status == HttpStatusCode.OK
    }

    suspend fun registrarVenta(venta: Venta): String {
        val response: String = client.post("$BASE_URL/ventas") {
            contentType(ContentType.Application.Json)
            setBody(venta)
        }.body()
        return response
    }
    // En tu ApiService.kt - agrega esta función
    suspend fun obtenerVentas(): List<Venta> {
        return client.get("$BASE_URL/ventas").body()
    }
}