package com.example.telasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.telasapp.ui.viewmodel.InventarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentaScreen(
    navController: NavController,
    rolloId: Int?,
    vm: InventarioViewModel = viewModel()
) {
    var metrosVendidos by remember { mutableStateOf("") }
    var vendedor by remember { mutableStateOf("") }
    var cliente by remember { mutableStateOf("") }

    val rollos by vm.rollos.collectAsState()
    val rollo = rollos.find { it.id == rolloId }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Vender ${rollo?.tipo_tela ?: "Rollo"} - ${rollo?.color ?: ""}")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (rollo == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Rollo no encontrado")
                }
            } else {
                // Información del rollo
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Rollo: ${rollo.tipo_tela} - ${rollo.color}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Código: ${rollo.codigo}")
                        Text("Metros disponibles: ${rollo.cantidad_restante}m")
                        Text(
                            "Estado: ${rollo.estado}",
                            color = when(rollo.estado) {
                                "Disponible" -> Color(0xFF2E7D32)
                                "Agotado" -> Color(0xFFD32F2F)
                                "Vendido" -> Color(0xFF1976D2)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }

                // Formulario de venta - SIMPLIFICADO (sin keyboardOptions)
                OutlinedTextField(
                    value = metrosVendidos,
                    onValueChange = {
                        // Validar que solo sean números
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            metrosVendidos = it
                        }
                    },
                    label = { Text("Metros a vender") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = vendedor,
                    onValueChange = { vendedor = it },
                    label = { Text("Nombre del vendedor *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = cliente,
                    onValueChange = { cliente = it },
                    label = { Text("Cliente (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            when {
                                metrosVendidos.isBlank() || vendedor.isBlank() -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Complete los campos requeridos")
                                    }
                                }
                                metrosVendidos.toDoubleOrNull() == null -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Ingrese una cantidad válida")
                                    }
                                }
                                metrosVendidos.toDouble() <= 0 -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("La cantidad debe ser mayor a 0")
                                    }
                                }
                                metrosVendidos.toDouble() > rollo.cantidad_restante.toDouble() -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("No hay suficientes metros disponibles")
                                    }
                                }
                                else -> {
                                    // Llamar a la función de venta del ViewModel
                                    vm.registrarVenta(
                                        rolloId = rollo.id ?: 0,
                                        metrosVendidos = metrosVendidos.toDouble(),
                                        vendedor = vendedor,
                                        cliente = if (cliente.isBlank()) null else cliente
                                    )
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Venta registrada correctamente")
                                        navController.popBackStack()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = rollo.estado == "Disponible"
                    ) {
                        Text("Vender")
                    }
                }

                // Botón para vender todo rápido
                if (rollo.estado == "Disponible") {
                    Button(
                        onClick = {
                            metrosVendidos = rollo.cantidad_restante
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text("VENDER TODO (${rollo.cantidad_restante}m)")
                    }
                }
            }
        }
    }
}