package com.example.telasapp.core.config


object AdminConfig {
    // Aquí agregas los correos que quieres que tengan permisos totales
    private val ADMIN_EMAILS = listOf(
        "admin2134@admin.com"
    )

    // Función que decide el rol según el email
    fun getRoleForEmail(email: String): String {
        return if (ADMIN_EMAILS.contains(email.lowercase().trim())) {
            "ADMIN"
        } else {
            "VENDEDOR"
        }
    }
}