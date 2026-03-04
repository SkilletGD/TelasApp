package com.example.telasapp.features.sales.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.core.components.TelasTextField
import com.example.telasapp.features.auth.viewmodel.AuthConfirmViewModel
import com.example.telasapp.features.auth.ui.components.AuthConfirmDialog
import com.example.telasapp.features.cart.data.models.CartItem
import com.example.telasapp.features.cart.viewmodel.CartViewModel
import com.example.telasapp.features.inventory.viewmodel.InventarioViewModel
import com.example.telasapp.features.sales.ui.components.RolloInfoCard
import com.example.telasapp.features.sales.ui.components.VentaActionsRow
import com.example.telasapp.features.sales.viewmodel.SalesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VentaScreen(
    navController: NavController,
    rolloId: Int?,
    salesVm: SalesViewModel,
    cartVm: CartViewModel,
    invVm: InventarioViewModel,
    authConfirmVm: AuthConfirmViewModel, // <-- NUEVO
    userEmail: String,                   // <-- NUEVO
    snackbarHostState: SnackbarHostState
) {
    // --- ESTADOS REACTIVOS ---
    var metrosVendidos by remember(rolloId) { mutableStateOf("") }
    var vendedor by remember(rolloId) { mutableStateOf("") }
    var cliente by remember(rolloId) { mutableStateOf("") }

    // Estado para saber si confirmamos "CARRITO" o "VENTA DIRECTA"
    var accionPendiente by remember { mutableStateOf<String?>(null) }

    val loteActual by salesVm.rolloActual.collectAsState()
    val isLoading by salesVm.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(rolloId) {
        rolloId?.let { salesVm.cargarRolloParaVenta(it) }
    }

    // --- LÓGICA DE NEGOCIO ---
    val metrosNum = metrosVendidos.toDoubleOrNull() ?: 0.0
    val lote = loteActual
    val totalDisponibleEnLote = lote?.detalles_rollos?.sumOf { it.metros_restantes.toDouble() } ?: 0.0

    val rollosAfectados = remember(metrosVendidos, lote) {
        val ids = mutableListOf<Int>()
        var acumulado = 0.0
        if (metrosNum > 0) {
            lote?.detalles_rollos?.filter { it.estado != "Agotado" }?.forEach { detalle ->
                if (acumulado < metrosNum) {
                    ids.add(detalle.id)
                    acumulado += detalle.metros_restantes.toDouble()
                }
            }
        }
        ids
    }

    val mensajeError = when {
        metrosNum > totalDisponibleEnLote -> "Stock insuficiente. Total lote: $totalDisponibleEnLote m"
        else -> null
    }

    val esValido = metrosNum > 0 && metrosNum <= totalDisponibleEnLote && vendedor.isNotBlank()

    // --- DIÁLOGO DE SEGURIDAD ---
    if (accionPendiente != null) {
        AuthConfirmDialog(
            email = userEmail,
            onDismiss = {
                accionPendiente = null
                authConfirmVm.reset()
            },
            onSuccess = {
                val accion = accionPendiente
                accionPendiente = null // Cerramos antes de ejecutar

                if (accion == "CARRITO") {
                    val nuevoItem = CartItem(
                        rolloId = rolloId!!,
                        loteCodigo = lote?.codigo ?: "N/A",
                        tipoTela = lote?.tipo_tela ?: "Desconocida",
                        color = lote?.color,
                        metros = metrosNum,
                        vendedor = vendedor,
                        cliente = cliente.ifBlank { null }
                    )
                    cartVm.agregar(nuevoItem)
                    invVm.cargarRollos()
                    navController.popBackStack()
                } else if (accion == "DIRECTA") {
                    salesVm.registrarVenta(
                        rolloId = rolloId!!,
                        metros = metrosNum,
                        vendedor = vendedor,
                        cliente = cliente
                    ) {
                        invVm.cargarRollos()
                        navController.popBackStack()
                    }
                }
            },
            authConfirmVm = authConfirmVm
        )
    }

    if (isLoading || lote == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RolloInfoCard(rollo = lote)

            Text("Rollos que se utilizarán (Asignación automática):", fontWeight = FontWeight.Bold)

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                lote.detalles_rollos?.filter { it.estado != "Agotado" }?.forEach { detalle ->
                    val seUsara = rollosAfectados.contains(detalle.id)
                    FilterChip(
                        selected = seUsara,
                        onClick = { },
                        label = { Text("R#${detalle.numero_rollo} (${detalle.metros_restantes}m)") }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text("Datos de la Venta:", fontWeight = FontWeight.Bold)

            TelasTextField(
                value = metrosVendidos,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) metrosVendidos = it },
                label = "Metros totales a vender",
                prefix = { Text("m ") },
                error = mensajeError,
                keyboardType = KeyboardType.Decimal
            )

            TelasTextField(value = vendedor, onValueChange = { vendedor = it }, label = "Vendedor *")
            TelasTextField(value = cliente, onValueChange = { cliente = it }, label = "Cliente (Opcional)")

            // --- BOTÓN CARRITO ---
            Button(
                onClick = { accionPendiente = "CARRITO" }, // Dispara el diálogo
                modifier = Modifier.fillMaxWidth(),
                enabled = esValido,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Añadir al Pedido y Volver")
            }

            // --- BOTÓN VENTA DIRECTA ---
            VentaActionsRow(
                onCancel = { navController.popBackStack() },
                onConfirm = { accionPendiente = "DIRECTA" }, // Dispara el diálogo
                enabled = esValido && !isLoading
            )
        }
    }
}