package com.example.telasapp.features.cart.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.core.components.EmptyState
import com.example.telasapp.features.cart.ui.components.CartBottomSummary
import com.example.telasapp.features.cart.ui.components.CartItemCard
import com.example.telasapp.features.cart.viewmodel.CartViewModel
import com.example.telasapp.features.sales.viewmodel.SalesViewModel
import com.example.telasapp.features.inventory.viewmodel.InventarioViewModel
import com.example.telasapp.navigation.Screen

@Composable
fun CartScreen(
    navController: NavController,
    cartVm: CartViewModel,
    salesVm: SalesViewModel,
    invVm: InventarioViewModel
) {
    val items by cartVm.items.collectAsState()
    // OBSERVAMOS el total de metros calculado en el ViewModel
    val totalMetros by cartVm.totalMetros.collectAsState()
    val isLoading by salesVm.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (items.isEmpty()) {
                EmptyState(
                    icon = "🛒",
                    title = "Carrito vacío",
                    description = "Agrega cortes de tela desde la pantalla de venta.",
                    onAction = { navController.navigate(Screen.Inventario.route) },
                    actionLabel = "Explorar Inventario"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp) // Un poco más de espacio para el summary
                ) {
                    item {
                        Text(
                            "Resumen del Pedido",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(items, key = { it.id }) { item ->
                        CartItemCard(
                            item = item,
                            onRemove = { if (!isLoading) cartVm.eliminar(item) }
                        )
                    }
                }
            }
        }

        if (items.isNotEmpty()) {
            CartBottomSummary(
                itemCount = items.size,
                // CAMBIO AQUÍ: Usamos la variable totalMetros que viene del ViewModel
                totalMetros = totalMetros,
                isLoading = isLoading,
                onCheckout = {
                    salesVm.registrarVentaMasiva(
                        items = items,
                        onSuccess = {
                            cartVm.limpiar()
                            invVm.cargarRollos()
                            navController.navigate(Screen.Inventario.route) {
                                popUpTo(Screen.Inventario.route) { inclusive = true }
                            }
                        }
                    )
                }
            )
        }
    }
}