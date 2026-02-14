package com.example.telasapp.features.registration.ui

import DatePickerField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.core.components.TelasTextField
import com.example.telasapp.data.models.Rollo
import com.example.telasapp.features.inventory.viewmodel.InventarioViewModel
import com.example.telasapp.features.registration.utils.RegistrationValidator
import com.example.telasapp.features.registration.viewmodel.RegistrationViewModel

@Composable
fun NuevoRolloScreen(
    navController: NavController,
    regVm: RegistrationViewModel,
    invVm: InventarioViewModel,
    snackbarHostState: SnackbarHostState
) {
    // 1. Estados de datos (Single Source of Truth en la UI)
    var tipoTela by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    var fechaCompra by remember { mutableStateOf("") }

    // 2. Estado de errores centralizado
    var errores by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // 3. Suscripción a eventos del ViewModel
    LaunchedEffect(Unit) {
        regVm.eventos.collect { snackbarHostState.showSnackbar(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Datos del Rollo",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Usando nuestro componente modular CORE
        TelasTextField(
            value = tipoTela,
            onValueChange = { tipoTela = it },
            label = "Tipo de Tela *",
            error = errores["tipoTela"]
        )

        TelasTextField(
            value = color,
            onValueChange = { color = it },
            label = "Color *",
            error = errores["color"]
        )

        TelasTextField(
            value = codigo,
            onValueChange = { codigo = it },
            label = "Código de Rollo *",
            error = errores["codigo"]
        )

        TelasTextField(
            value = cantidad,
            onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) cantidad = it },
            label = "Cantidad Inicial (m) *",
            error = errores["cantidad"],
            keyboardType = KeyboardType.Decimal,
            prefix = { Text("m ") }
        )

        // Campo opcional (no necesita validación de error obligatoria)
        TelasTextField(
            value = proveedor,
            onValueChange = { proveedor = it },
            label = "Proveedor (Opcional)"
        )

        // Componente modular de FECHA
        DatePickerField(fechaCompra) { fechaCompra = it }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de acción
        Button(
            onClick = {
                val validacion = RegistrationValidator.validarFormulario(tipoTela, color, codigo, cantidad)
                errores = validacion

                if (validacion.isEmpty()) {
                    val nuevoRollo = Rollo(
                        tipo_tela = tipoTela.trim(),
                        color = color.trim(),
                        codigo = codigo.trim(),
                        cantidad_total = cantidad,
                        cantidad_restante = cantidad,
                        proveedor = proveedor.trim().takeIf { it.isNotBlank() },
                        fecha_compra = fechaCompra,
                        registrado_por = "Admin"
                    )

                    regVm.agregarRollo(nuevoRollo) {
                        invVm.cargarRollos()
                        navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Guardar Rollo")
        }
    }
}