package com.example.telasapp.features.auth.data.models


import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val idToken: String? = null,      // El token de sesión
    val email: String? = null,       // El correo del usuario
    val refreshToken: String? = null, // Token para refrescar la sesión
    val expiresIn: String? = null,    // Tiempo de expiración (segundos)
    val localId: String? = null,      // El ID único del usuario en Firebase
    val registered: Boolean? = null   // Indica si el usuario ya existe
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)