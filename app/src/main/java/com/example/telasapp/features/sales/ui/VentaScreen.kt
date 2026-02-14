package com.example.telasapp.features.sales.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.telasapp.core.components.TelasTextField
import com.example.telasapp.features.inventory.viewmodel.InventarioViewModel
import com.example.telasapp.features.sales.ui.components.RolloInfoCard
import com.example.telasapp.features.sales.ui.components.VentaActionsRow
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

    val rollo by salesVm.rolloActual.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(rolloId) {
        rolloId?.let { salesVm.cargarRolloParaVenta(it) }
    }

    if (rollo == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
    } else {
        val r = rollo!!
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Componente extraído
            RolloInfoCard(rollo = r)

            Text("Detalles de la venta", style = MaterialTheme.typography.labelLarge)

            // 2. Usando tu componente modular compartido
            TelasTextField(
                value = metrosVendidos,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) metrosVendidos = it },
                label = "Metros a vender *",
                prefix = { Text("m ") },
                keyboardType = KeyboardType.Decimal
            )

            TelasTextField(
                value = vendedor,
                onValueChange = { vendedor = it },
                label = "Nombre del vendedor *"
            )

            TelasTextField(
                value = cliente,
                onValueChange = { cliente = it },
                label = "Cliente (opcional)"
            )

            if (r.estado == "Disponible") {
                OutlinedButton(
                    onClick = { metrosVendidos = r.cantidad_restante.toString() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("VENDER TODO EL ROLLO (${r.cantidad_restante}m)")
                }
            }

            Spacer(Modifier.weight(1f))

            // 3. Botones de acción
            VentaActionsRow(
                onCancel = { navController.popBackStack() },
                onConfirm = {
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
                            salesVm.registrarVenta(r.id!!, cantNum, vendedor, cliente.ifBlank { null }) {
                                invVm.cargarRollos()
                                navController.popBackStack()
                            }
                        }
                    }
                },
                enabled = r.estado == "Disponible"
            )
        }
    }
}
