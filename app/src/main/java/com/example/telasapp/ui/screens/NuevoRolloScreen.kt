package com.example.telasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.telasapp.data.models.Rollo
import com.example.telasapp.ui.viewmodel.InventarioViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoRolloScreen(
    navController: NavController,
    vm: InventarioViewModel
) {
    var tipoTela by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    var fechaCompra by remember { mutableStateOf("") }

    // Estados para el DatePicker
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Estados para errores
    var errorTipoTela by remember { mutableStateOf("") }
    var errorColor by remember { mutableStateOf("") }
    var errorCodigo by remember { mutableStateOf("") }
    var errorCantidad by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Función para formatear fecha bonita
    fun formatearFechaBonita(fecha: String): String {
        return try {
            val fechaLocal = LocalDate.parse(fecha, DateTimeFormatter.ISO_DATE)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            fechaLocal.format(formatter)
        } catch (e: Exception) {
            fecha
        }
    }

    // Función para manejar la fecha seleccionada
    fun onFechaSeleccionada() {
        datePickerState.selectedDateMillis?.let { millis ->
            val localDate = java.time.Instant.ofEpochMilli(millis)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()

            fechaCompra = localDate.format(DateTimeFormatter.ISO_DATE)
        }
    }

    // Función para validar todos los campos
    fun validarFormulario(): Boolean {
        var esValido = true

        // Reiniciar errores
        errorTipoTela = ""
        errorColor = ""
        errorCodigo = ""
        errorCantidad = ""

        // Validar Tipo de Tela (máximo 50 caracteres)
        if (tipoTela.isBlank()) {
            errorTipoTela = "El tipo de tela es obligatorio"
            esValido = false
        } else if (tipoTela.length > 50) {
            errorTipoTela = "Máximo 50 caracteres"
            esValido = false
        }

        // Validar Color (máximo 30 caracteres)
        if (color.isBlank()) {
            errorColor = "El color es obligatorio"
            esValido = false
        } else if (color.length > 30) {
            errorColor = "Máximo 30 caracteres"
            esValido = false
        }

        // Validar Código (máximo 20 caracteres)
        if (codigo.isBlank()) {
            errorCodigo = "El código es obligatorio"
            esValido = false
        } else if (codigo.length > 20) {
            errorCodigo = "Máximo 20 caracteres"
            esValido = false
        }

        // Validar Cantidad (entre 0.1 y 1000 metros)
        if (cantidad.isBlank()) {
            errorCantidad = "La cantidad es obligatoria"
            esValido = false
        } else {
            val cantidadNum = cantidad.toDoubleOrNull()
            when {
                cantidadNum == null -> {
                    errorCantidad = "Debe ser un número válido"
                    esValido = false
                }
                cantidadNum <= 0 -> {
                    errorCantidad = "Debe ser mayor a 0"
                    esValido = false
                }
                cantidadNum > 1000 -> {
                    errorCantidad = "Máximo 1000 metros por rollo"
                    esValido = false
                }
                cantidadNum < 0.1 -> {
                    errorCantidad = "Mínimo 0.1 metros"
                    esValido = false
                }
            }
        }

        return esValido
    }

    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onFechaSeleccionada()
                        showDatePicker = false
                    }
                ) {
                    Text("Seleccionar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Nuevo Rollo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (validarFormulario()) {
                        val rollo = Rollo(
                            tipo_tela = tipoTela.trim(),
                            color = color.trim(),
                            codigo = codigo.trim(),
                            cantidad_total = cantidad,
                            cantidad_restante = cantidad,
                            proveedor = proveedor.trim().takeIf { it.isNotBlank() },
                            fecha_compra = fechaCompra.trim().takeIf { it.isNotBlank() },
                            registrado_por = "Usuario"
                        )

                        scope.launch {
                            try {
                                vm.agregarRollo(rollo)
                                snackbarHostState.showSnackbar("✅ Rollo registrado correctamente")
                                navController.popBackStack()
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("❌ Error al registrar el rollo: ${e.message}")
                            }
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("❌ Revise los campos marcados en rojo")
                        }
                    }
                }
            ) {
                Text("💾")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tipo de Tela
            OutlinedTextField(
                value = tipoTela,
                onValueChange = {
                    tipoTela = it
                    if (it.length > 50) {
                        errorTipoTela = "Máximo 50 caracteres"
                    } else {
                        errorTipoTela = ""
                    }
                },
                label = { Text("Tipo de Tela *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorTipoTela.isNotBlank(),
                supportingText = {
                    if (errorTipoTela.isNotBlank()) {
                        Text(errorTipoTela, color = Color.Red)
                    } else {
                        Text("${tipoTela.length}/50 caracteres")
                    }
                }
            )

            // Color
            OutlinedTextField(
                value = color,
                onValueChange = {
                    color = it
                    if (it.length > 30) {
                        errorColor = "Máximo 30 caracteres"
                    } else {
                        errorColor = ""
                    }
                },
                label = { Text("Color *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorColor.isNotBlank(),
                supportingText = {
                    if (errorColor.isNotBlank()) {
                        Text(errorColor, color = Color.Red)
                    } else {
                        Text("${color.length}/30 caracteres")
                    }
                }
            )

            // Código
            OutlinedTextField(
                value = codigo,
                onValueChange = {
                    codigo = it
                    if (it.length > 20) {
                        errorCodigo = "Máximo 20 caracteres"
                    } else {
                        errorCodigo = ""
                    }
                },
                label = { Text("Código *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorCodigo.isNotBlank(),
                supportingText = {
                    if (errorCodigo.isNotBlank()) {
                        Text(errorCodigo, color = Color.Red)
                    } else {
                        Text("${codigo.length}/20 caracteres")
                    }
                }
            )

            // Cantidad (metros)
            OutlinedTextField(
                value = cantidad,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        cantidad = it
                        errorCantidad = ""
                    }
                },
                label = { Text("Cantidad (metros) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorCantidad.isNotBlank(),
                supportingText = {
                    if (errorCantidad.isNotBlank()) {
                        Text(errorCantidad, color = Color.Red)
                    } else {
                        Text("Mínimo: 0.1m - Máximo: 1000m")
                    }
                }
            )

            // Proveedor
            OutlinedTextField(
                value = proveedor,
                onValueChange = {
                    proveedor = it
                    if (it.length > 100) {
                        proveedor = it.take(100)
                    }
                },
                label = { Text("Proveedor (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("${proveedor.length}/100 caracteres")
                }
            )

            // Fecha de compra - CON CALENDARIO
            OutlinedTextField(
                value = if (fechaCompra.isNotBlank()) formatearFechaBonita(fechaCompra) else "",
                onValueChange = { }, // No permitir edición manual
                label = { Text("Fecha de compra (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = true,
                supportingText = {
                    Text("Toque para seleccionar fecha")
                },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Text("📅")
                    }
                }
            )

            // Botón alternativo para abrir calendario
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE3F2FD),
                    contentColor = Color(0xFF1976D2)
                )
            ) {
                Text("📅 Seleccionar Fecha de Compra")
            }

            // Mostrar fecha seleccionada
            if (fechaCompra.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Fecha seleccionada:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            formatearFechaBonita(fechaCompra),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            // Información de validación
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("📋 Información de validación:",
                        style = MaterialTheme.typography.labelMedium)
                    Text("• Campos con * son obligatorios",
                        style = MaterialTheme.typography.bodySmall)
                    Text("• Cantidad: 0.1 - 1000 metros",
                        style = MaterialTheme.typography.bodySmall)
                    Text("• Use el calendario para seleccionar fecha",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}