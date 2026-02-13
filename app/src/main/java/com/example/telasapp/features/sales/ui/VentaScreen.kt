package com.example.telasapp.features.sales.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.telasapp.features.inventory.viewmodel.InventarioViewModel
import com.example.telasapp.features.sales.viewmodel.SalesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentaScreen(
    navController: NavController,
    rolloId: Int?,
    salesVm: SalesViewModel,
    invVm: InventarioViewModel,
    snackbarHostState: SnackbarHostState
) {
    var metrosVendidos by remember { mutableStateOf("") }
    var vendedor by remember { mutableStateOf("") }
    var cliente by remember { mutableStateOf("") }

    // Obtenemos el rollo del SalesViewModel
    val rollo by salesVm.rolloActual.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Cargar datos al iniciar
    LaunchedEffect(rolloId) {
        rolloId?.let { salesVm.cargarRolloParaVenta(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (rollo == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // Muestra carga mientras llega el dato
            }
        } else {
            // --- LA SOLUCIÓN ESTÁ AQUÍ ---
            // Creamos una variable local 'r' que Kotlin sí puede validar (Smart Cast)
            val r = rollo!!

            // 1. Tarjeta informativa
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${r.tipo_tela} - ${r.color}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Código: ${r.codigo}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Metros disponibles: ", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "${r.cantidad_restante}m",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 2. Formulario
            Text("Detalles de la venta", style = MaterialTheme.typography.labelLarge)

            OutlinedTextField(
                value = metrosVendidos,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) metrosVendidos = it },
                label = { Text("Metros a vender *") },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("m ") }
            )

            OutlinedTextField(
                value = vendedor,
                onValueChange = { vendedor = it },
                label = { Text("Nombre del vendedor *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cliente,
                onValueChange = { cliente = it },
                label = { Text("Cliente (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Botón venta rápida
            if (r.estado == "Disponible") {
                OutlinedButton(
                    onClick = { metrosVendidos = r.cantidad_restante.toString() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("VENDER TODO EL ROLLO (${r.cantidad_restante}m)")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 4. Botones finales
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        val cantNum = metrosVendidos.toDoubleOrNull()
                        val disponibleNum = r.cantidad_restante.toDoubleOrNull() ?: 0.0

                        when {
                            metrosVendidos.isBlank() || vendedor.isBlank() -> {
                                scope.launch { snackbarHostState.showSnackbar("❌ Complete campos") }
                            }
                            cantNum == null || cantNum <= 0 -> {
                                scope.launch { snackbarHostState.showSnackbar("❌ Cantidad no válida") }
                            }
                            cantNum > disponibleNum -> {
                                scope.launch { snackbarHostState.showSnackbar("❌ Stock insuficiente") }
                            }
                            else -> {
                                // AQUÍ ESTÁ EL CAMBIO:
                                // Usamos !! porque ya verificamos arriba que 'r' no es null
                                salesVm.registrarVenta(
                                    rolloId = r.id!!, // Se agrega !! para convertir Int? a Int
                                    metros = cantNum,
                                    vendedor = vendedor,
                                    cliente = cliente.ifBlank { null }
                                ) {
                                    invVm.cargarRollos()
                                    navController.popBackStack()
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = r.estado == "Disponible"
                ) {
                    Text("Registrar Venta")
                }
            }
        }
    }
}