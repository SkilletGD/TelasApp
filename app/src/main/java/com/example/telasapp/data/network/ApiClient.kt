package com.example.telasapp.data.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                coerceInputValues = true  // ¡AGREGA ESTA LÍNEA!
                explicitNulls = false     // ¡AGREGA ESTA LÍNEA!
            })
        }
    }

    const val BASE_URL = "https://bd-telas-app.onrender.com"
}
