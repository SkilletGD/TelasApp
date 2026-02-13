package com.example.telasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.telasapp.ui.viewmodel.InventarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentaScreen(
    navController: NavController,
    rolloId: Int?,
    vm: InventarioViewModel,
    snackbarHostState: SnackbarHostState
) {
    var metrosVendidos by remember { mutableStateOf("") }
    var vendedor by remember { mutableStateOf("") }
    var cliente by remember { mutableStateOf("") }

    val rollos by vm.rollos.collectAsState()
    val rollo = rollos.find { it.id == rolloId }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // CONTENIDO PRINCIPAL
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (rollo == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Rollo no encontrado")
            }
        } else {
            // 1. Tarjeta informativa del producto
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${rollo.tipo_tela} - ${rollo.color}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Código: ${rollo.codigo}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Metros disponibles: ", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "${rollo.cantidad_restante}m",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 2. Formulario de venta
            Text("Detalles de la venta", style = MaterialTheme.typography.labelLarge)

            OutlinedTextField(
                value = metrosVendidos,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        metrosVendidos = it
                    }
                },
                label = { Text("Metros a vender *") },
                placeholder = { Text("Ej: 5.5") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                prefix = { Text("m ") }
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

            // 3. Botón para vender todo rápido
            if (rollo.estado == "Disponible") {
                OutlinedButton(
                    onClick = { metrosVendidos = rollo.cantidad_restante },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("VENDER TODO EL ROLLO (${rollo.cantidad_restante}m)")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 4. Botones de acción final
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        val cantNum = metrosVendidos.toDoubleOrNull()
                        val disponibleNum = rollo.cantidad_restante.toDoubleOrNull() ?: 0.0

                        when {
                            metrosVendidos.isBlank() || vendedor.isBlank() -> {
                                scope.launch { snackbarHostState.showSnackbar("❌ Complete los campos requeridos") }
                            }
                            cantNum == null || cantNum <= 0 -> {
                                scope.launch { snackbarHostState.showSnackbar("❌ Ingrese una cantidad válida") }
                            }
                            cantNum > disponibleNum -> {
                                scope.launch { snackbarHostState.showSnackbar("❌ No hay suficientes metros") }
                            }
                            else -> {
                                // 1. Ejecutamos la venta en el ViewModel
                                vm.registrarVenta(
                                    rolloId = rollo.id ?: 0,
                                    metrosVendidos = cantNum,
                                    vendedor = vendedor,
                                    cliente = if (cliente.isBlank()) null else cliente
                                )

                                // 2. REGRESAR PRIMERO
                                navController.popBackStack()

                                // 3. LANZAR EL MENSAJE DESDE EL VIEWMODEL
                                // Usamos el scope del VM porque ese NO se muere al cerrar la pantalla
                                vm.viewModelScope.launch {
                                    snackbarHostState.showSnackbar("✅ Venta registrada correctamente")
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = rollo.estado == "Disponible"
                ) {
                    Text("Registrar Venta")
                }
            }
        }
    }
}