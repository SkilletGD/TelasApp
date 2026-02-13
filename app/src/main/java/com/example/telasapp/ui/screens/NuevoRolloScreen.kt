package com.example.telasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    vm: InventarioViewModel,
    snackbarHostState: SnackbarHostState // Recibido del Scaffold Global
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
    val scrollState = rememberScrollState()

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

    // --- CONTENIDO DE LA PANTALLA ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState), // Scroll para pantallas pequeñas
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Datos del Rollo",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = tipoTela, onValueChange = { tipoTela = it },
            label = { Text("Tipo de Tela *") }, modifier = Modifier.fillMaxWidth(),
            isError = errorTipoTela.isNotBlank(),
            supportingText = { if (errorTipoTela.isNotBlank()) Text(errorTipoTela, color = Color.Red) }
        )

        OutlinedTextField(
            value = color, onValueChange = { color = it },
            label = { Text("Color *") }, modifier = Modifier.fillMaxWidth(),
            isError = errorColor.isNotBlank(),
            supportingText = { if (errorColor.isNotBlank()) Text(errorColor, color = Color.Red) }
        )

        OutlinedTextField(
            value = codigo, onValueChange = { codigo = it },
            label = { Text("Código de Rollo *") }, modifier = Modifier.fillMaxWidth(),
            isError = errorCodigo.isNotBlank(),
            supportingText = { if (errorCodigo.isNotBlank()) Text(errorCodigo, color = Color.Red) }
        )

        OutlinedTextField(
            value = cantidad,
            onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) cantidad = it },
            label = { Text("Cantidad Inicial (Metros) *") }, modifier = Modifier.fillMaxWidth(),
            isError = errorCantidad.isNotBlank(),
            supportingText = { if (errorCantidad.isNotBlank()) Text(errorCantidad, color = Color.Red) }
        )

        OutlinedTextField(
            value = proveedor, onValueChange = { proveedor = it },
            label = { Text("Proveedor (Opcional)") }, modifier = Modifier.fillMaxWidth()
        )

        // Selector de Fecha
        OutlinedCard(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (fechaCompra.isEmpty()) "Seleccionar Fecha de Compra" else "Compra: ${formatearFechaBonita(fechaCompra)}")
                Text("📅")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BOTÓN DE GUARDAR (Sustituye al FAB para mejor usabilidad en formularios)
        Button(
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
                        registrado_por = "Admin"
                    )

                    scope.launch {
                        try {
                            vm.agregarRollo(rollo)
                            navController.popBackStack()
                            snackbarHostState.showSnackbar("✅ Rollo registrado con éxito")
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("❌ Error: ${e.message}")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Guardar Rollo")
        }
    }
}