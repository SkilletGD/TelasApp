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
import com.example.telasapp.features.cart.data.models.CartItem
import com.example.telasapp.features.cart.viewmodel.CartViewModel
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
    cartVm: CartViewModel,
    invVm: InventarioViewModel,
    snackbarHostState: SnackbarHostState
) {
    var metrosVendidos by remember { mutableStateOf("") }
    var vendedor by remember { mutableStateOf("") }
    var cliente by remember { mutableStateOf("") }

    // NUEVO: Estado para el rollo físico seleccionado dentro del lote
    var rolloSeleccionadoId by remember { mutableStateOf<Int?>(null) }
    var metrosDisponiblesEnRollo by remember { mutableStateOf(0.0) }

    val lote by salesVm.rolloActual.collectAsState() // "rollo" ahora es el Lote completo
    val scope = rememberCoroutineScope()

    LaunchedEffect(rolloId) {
        rolloId?.let { salesVm.cargarRolloParaVenta(it) }
    }

    if (lote == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
    } else {
        val l = lote!!
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RolloInfoCard(rollo = l)

            // --- NUEVA SECCIÓN: SELECCIÓN DE ROLLO FÍSICO ---
            Text("Seleccione el rollo físico del cual cortará:", style = MaterialTheme.typography.labelLarge)

            l.detalles_rollos?.filter { it.estado != "Agotado" }?.forEach { detalle ->
                FilterChip(
                    selected = rolloSeleccionadoId == detalle.id,
                    onClick = {
                        rolloSeleccionadoId = detalle.id
                        metrosDisponiblesEnRollo = detalle.metros_restantes
                    },
                    label = { Text("Rollo #${detalle.numero_rollo} (${detalle.metros_restantes}m)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (rolloSeleccionadoId == null) {
                Text("⚠️ Debe seleccionar un rollo antes de continuar", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // --- ENTRADA DE METROS ---
            TelasTextField(
                value = metrosVendidos,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) metrosVendidos = it },
                label = "Metros a vender *",
                prefix = { Text("m ") },
                enabled = rolloSeleccionadoId != null,
                keyboardType = KeyboardType.Decimal
            )

            // Botón rápido para vender el rollo seleccionado completo
            if (rolloSeleccionadoId != null) {
                OutlinedButton(
                    onClick = { metrosVendidos = metrosDisponiblesEnRollo.toString() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("VENDER TODO ESTE ROLLO ($metrosDisponiblesEnRollo m)")
                }
            }

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

            Spacer(Modifier.weight(1f))

            // --- ACCIONES ---
            VentaActionsRow(
                onCancel = { navController.popBackStack() },
                onConfirm = {
                    val cantNum = metrosVendidos.toDoubleOrNull()

                    when {
                        rolloSeleccionadoId == null -> {
                            scope.launch { snackbarHostState.showSnackbar("❌ Seleccione un rollo físico") }
                        }
                        metrosVendidos.isBlank() || vendedor.isBlank() -> {
                            scope.launch { snackbarHostState.showSnackbar("❌ Complete campos") }
                        }
                        cantNum == null || cantNum <= 0 -> {
                            scope.launch { snackbarHostState.showSnackbar("❌ Cantidad no válida") }
                        }
                        cantNum > metrosDisponiblesEnRollo -> {
                            scope.launch { snackbarHostState.showSnackbar("❌ El rollo solo tiene $metrosDisponiblesEnRollo m") }
                        }
                        else -> {
                            // IMPORTANTE: Enviamos rolloSeleccionadoId (el ID del rollo físico)
                            salesVm.registrarVenta(
                                rolloId = rolloSeleccionadoId!!,
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
                enabled = l.estado == "Disponible"
            )
        }
    }
}