package com.example.telasapp.features.auth.data.repository

import com.example.telasapp.core.config.AdminConfig
import com.example.telasapp.data.preferences.TokenManager
import com.example.telasapp.features.auth.data.models.LoginRequest
import com.example.telasapp.features.auth.data.models.LoginResponse
// IMPORTANTE: Asegúrate de que TokenManager esté en este paquete o impórtalo correctamente
// import com.example.telasapp.data.preferences.TokenManager

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType // ESTE ES EL CORRECTO
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.client.request.setBody


class AuthRepository(private val client: HttpClient, private val tokenManager: TokenManager) {
    private val apiKey = "AIzaSyApPWZ35D1U9l56lpeApxqazOh7ycyv9bw"
    private val url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$apiKey"

    suspend fun login(email: String, pass: String): Result<LoginResponse> {
        return try {
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, pass))
            }

            if (response.status == HttpStatusCode.OK) {
                val loginData = response.body<LoginResponse>()

                // 1. Determinamos el rol usando la lista blanca
                val userRole = AdminConfig.getRoleForEmail(email)

                // 2. Guardamos todo en el DataStore
                loginData.idToken?.let { token ->
                    tokenManager.saveUserData(
                        token = token,
                        email = email,
                        role = userRole
                    )
                }

                Result.success(loginData)
            } else {
                Result.failure(Exception("Usuario o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.localizedMessage}"))
        }
    }
}