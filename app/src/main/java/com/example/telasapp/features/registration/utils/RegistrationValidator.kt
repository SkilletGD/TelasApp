package com.example.telasapp.features.registration.utils

object RegistrationValidator {
    fun validarLote(
        tipoTela: String,
        color: String,
        codigo: String,
        metrosPorRollo: String,
        cantidadRollos: String,
        precio: String,
        fechaCompra: String
    ): Map<String, String> {
        val errores = mutableMapOf<String, String>()

        // 1. Validaciones de Texto
        if (tipoTela.isBlank()) errores["tipoTela"] = "El tipo de tela es obligatorio"

        if (color.isBlank()) errores["color"] = "El color es obligatorio"

        if (codigo.isBlank()) errores["codigo"] = "El código es obligatorio"

        // 2. Validación de Metros por Rollo (Decimal)
        val mtrNum = metrosPorRollo.toDoubleOrNull()
        when {
            metrosPorRollo.isBlank() -> errores["metrosPorRollo"] = "Obligatorio"
            mtrNum == null -> errores["metrosPorRollo"] = "Número no válido"
            mtrNum <= 0 -> errores["metrosPorRollo"] = "Debe ser > 0"
        }

        // 3. Validación de Cantidad de Rollos (Entero)
        val cantNum = cantidadRollos.toIntOrNull()
        when {
            cantidadRollos.isBlank() -> errores["cantidadRollos"] = "Obligatorio"
            cantNum == null -> errores["cantidadRollos"] = "Número no válido"
            cantNum <= 0 -> errores["cantidadRollos"] = "Mínimo 1 rollo"
            cantNum > 100 -> errores["cantidadRollos"] = "Máximo 100 rollos por lote"
        }

        // 4. Validación de Precio (Decimal)
        val precioNum = precio.toDoubleOrNull()
        when {
            precio.isBlank() -> errores["precio"] = "El precio es obligatorio"
            precioNum == null -> errores["precio"] = "Precio no válido"
            precioNum <= 0 -> errores["precio"] = "Debe ser mayor a 0"
        }

        // 5. Validación de Fecha
        if (fechaCompra.isBlank()) {
            errores["fechaCompra"] = "Selecciona una fecha"
        }

        return errores
    }
}