package com.example.telasapp.features.cart.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    val isLoading by salesVm.isLoading.collectAsState()

    // Usamos una Column simple en lugar de un Scaffold interno
    Column(modifier = Modifier.fillMaxSize()) {

        // 1. Indicador de carga (si se está procesando la venta masiva)
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // 2. Contenido principal (Lista o Estado Vacío)
        Box(modifier = Modifier.weight(1f)) {
            if (items.isEmpty()) {
                EmptyState(
                    icon = "🛒",
                    title = "Carrito vacío",
                    description = "Agrega rollos desde la pantalla de venta.",
                    onAction = { navController.navigate(Screen.Inventario.route) },
                    actionLabel = "Explorar Inventario"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Text(
                            "Productos en preventa",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    items(items) { item ->
                        CartItemCard(
                            item = item,
                            onRemove = { cartVm.eliminar(item) }
                        )
                    }
                }
            }
        }

        // 3. Resumen de compra (Se queda pegado abajo, arriba de la BottomBar general)
        if (items.isNotEmpty()) {
            CartBottomSummary(
                itemCount = items.size,
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