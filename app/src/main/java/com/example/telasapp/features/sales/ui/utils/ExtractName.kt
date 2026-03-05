package com.example.telasapp.features.sales.ui.utils

// Función sencilla para limpiar el email
fun ExtractName(email: String?): String {
    if (email.isNullOrBlank()) return ""
    return email.substringBefore("@") // "pedro1212@telas.com" -> "pedro1212"
        .replaceFirstChar { it.uppercase() } // Opcional: "Pedro1212"
}