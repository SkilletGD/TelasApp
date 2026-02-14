package com.example.telasapp.features.registration.utils

object RegistrationValidator {
    fun validarFormulario(
        tipoTela: String,
        color: String,
        codigo: String,
        cantidad: String
    ): Map<String, String> {
        val errores = mutableMapOf<String, String>()

        if (tipoTela.isBlank()) errores["tipoTela"] = "El tipo de tela es obligatorio"
        else if (tipoTela.length > 50) errores["tipoTela"] = "Máximo 50 caracteres"

        if (color.isBlank()) errores["color"] = "El color es obligatorio"

        if (codigo.isBlank()) errores["codigo"] = "El código es obligatorio"

        val cantidadNum = cantidad.toDoubleOrNull()
        when {
            cantidad.isBlank() -> errores["cantidad"] = "La cantidad es obligatoria"
            cantidadNum == null -> errores["cantidad"] = "Número no válido"
            cantidadNum <= 0 -> errores["cantidad"] = "Debe ser mayor a 0"
            cantidadNum > 1000 -> errores["cantidad"] = "Máximo 1000 metros"
        }

        return errores
    }
}