package com.example.telasapp.data.models

enum class UserRole {
    ADMIN,
    VENDEDOR
}

data class User(
    val email: String,
    val role: UserRole
)