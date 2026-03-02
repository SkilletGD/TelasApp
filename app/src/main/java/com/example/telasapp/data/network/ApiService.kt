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

    // Obtiene la lista de lotes (vista general)
    suspend fun obtenerRollos(): List<Rollo> {
        return client.get("$BASE_URL/rollos").body()
    }

    // NUEVO: Obtiene el inventario desglosado por cada rollo físico
    // Útil si quieres una pantalla que muestre "Rollo 1, Rollo 2..."
    suspend fun obtenerInventarioDetallado(): List<Rollo> {
        return client.get("$BASE_URL/inventario/detalle").body()
    }

    suspend fun crearRollo(rollo: Rollo): Rollo {
        return client.post("$BASE_URL/rollos") {
            contentType(ContentType.Application.Json)
            setBody(rollo)
        }.body()
    }

    suspend fun actualizarRollo(rollo: Rollo): Boolean {
        val response = client.put("$BASE_URL/rollos/${rollo.id}") {
            contentType(ContentType.Application.Json)
            setBody(rollo)
        }
        return response.status.isSuccess()
    }

    suspend fun eliminarRollo(rolloId: Int): Boolean {
        val response: HttpResponse = client.delete("$BASE_URL/rollos/$rolloId")
        return response.status.isSuccess()
    }

    suspend fun registrarVenta(venta: Venta): String {
        // Cambiamos a HttpResponse para manejar mejor los errores de stock de tu nueva API
        val response = client.post("$BASE_URL/ventas") {
            contentType(ContentType.Application.Json)
            setBody(venta)
        }
        return response.bodyAsText() // Devolvemos el mensaje de éxito o error del servidor
    }

    suspend fun obtenerVentas(): List<Venta> {
        return client.get("$BASE_URL/ventas").body()
    }

    suspend fun obtenerRolloPorId(id: Int): Rollo {
        return client.get("$BASE_URL/rollos/$id").body()
    }
}