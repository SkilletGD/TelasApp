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
import com.example.telasapp.features.auth.viewmodel.AuthConfirmViewModel
import com.example.telasapp.features.auth.ui.components.AuthConfirmDialog
import com.example.telasapp.features.inventory.viewmodel.InventarioViewModel
import com.example.telasapp.features.registration.utils.RegistrationValidator
import com.example.telasapp.features.registration.viewmodel.RegistrationViewModel

@Composable
fun NuevoRolloScreen(
    navController: NavController,
    regVm: RegistrationViewModel,
    invVm: InventarioViewModel,
    authConfirmVm: AuthConfirmViewModel, // PASO 1: Inyectar el ViewModel de confirmación
    userEmail: String, // PASO 2: Necesitamos el correo del usuario logueado
    snackbarHostState: SnackbarHostState
) {
    var tipoTela by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }

    // NUEVOS CAMPOS PARA EL LOTE
    var metrosPorRollo by remember { mutableStateOf("") }
    var cantidadRollos by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    var proveedor by remember { mutableStateOf("") }
    var fechaCompra by remember { mutableStateOf("") }

    var errores by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // PASO 3: Estado para controlar el diálogo
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        regVm.eventos.collect { snackbarHostState.showSnackbar(it) }
    }

    // PASO 4: Mostrar el Diálogo si el estado es true
    if (mostrarConfirmacion) {
        AuthConfirmDialog(
            email = userEmail,
            onDismiss = {
                mostrarConfirmacion = false
                authConfirmVm.reset()
            },
            onSuccess = {
                mostrarConfirmacion = false
                // Aquí ejecutamos la lógica real de guardado que ya tenías
                val nuevoLote = Rollo(
                    tipo_tela = tipoTela.trim(),
                    color = color.trim(),
                    codigo = codigo.trim(),
                    metros_por_rollo = metrosPorRollo.toDouble(),
                    cantidad_rollos = cantidadRollos.toInt(),
                    precio = precio.toDouble(),
                    proveedor = proveedor.trim().takeIf { it.isNotBlank() },
                    fecha_compra = fechaCompra,
                    registrado_por = userEmail // Usamos el correo real
                )

                regVm.agregarRollo(nuevoLote) {
                    invVm.cargarRollos()
                    navController.popBackStack()
                }
            },
            authConfirmVm = authConfirmVm
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Registro de Nuevo Lote",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        TelasTextField(
            value = tipoTela,
            onValueChange = { tipoTela = it },
            label = "Tipo de Tela *",
            error = errores["tipoTela"]
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) {
                TelasTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = "Color *",
                    error = errores["color"]
                )
            }
            Box(Modifier.weight(1f)) {
                TelasTextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = "Código Lote *",
                    error = errores["codigo"]
                )
            }
        }

        // --- SECCIÓN DE MEDIDAS DEL LOTE ---
        Text("Configuración del Lote", style = MaterialTheme.typography.labelLarge)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) {
                TelasTextField(
                    value = metrosPorRollo,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) metrosPorRollo = it },
                    label = "Metros c/u *",
                    keyboardType = KeyboardType.Decimal,
                    error = errores["metrosPorRollo"]
                )
            }
            Box(Modifier.weight(1f)) {
                TelasTextField(
                    value = cantidadRollos,
                    onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) cantidadRollos = it },
                    label = "Cant. Rollos *",
                    keyboardType = KeyboardType.Number,
                    error = errores["cantidadRollos"]
                )
            }
        }

        TelasTextField(
            value = precio,
            onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) precio = it },
            label = "Precio por Metro *",
            keyboardType = KeyboardType.Decimal,
            prefix = { Text("$ ") },
            error = errores["precio"]
        )

        TelasTextField(
            value = proveedor,
            onValueChange = { proveedor = it },
            label = "Proveedor (Opcional)"
        )

        DatePickerField(fechaCompra) { fechaCompra = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Primero validamos los campos de texto
                val validacion = RegistrationValidator.validarLote(
                    tipoTela, color, codigo, metrosPorRollo, cantidadRollos, precio, fechaCompra
                )
                errores = validacion

                if (validacion.isEmpty()) {
                    // PASO 5: En lugar de guardar directo, disparamos el diálogo
                    mostrarConfirmacion = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Crear Lote de Rollos")
        }
    }
}