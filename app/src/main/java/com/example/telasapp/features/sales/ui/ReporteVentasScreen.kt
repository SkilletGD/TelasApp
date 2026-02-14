package com.example.telasapp.features.sales.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.telasapp.core.components.EmptyState // Importamos el genérico
import com.example.telasapp.features.inventory.ui.components.ErrorCard
import com.example.telasapp.features.sales.ui.components.SalesSummaryCard
import com.example.telasapp.features.sales.ui.components.VentaItem
import com.example.telasapp.features.sales.viewmodel.SalesViewModel

@Composable
fun ReporteVentasScreen(vm: SalesViewModel) {
    val ventas by vm.ventas.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()

    // Carga inicial
    LaunchedEffect(Unit) { vm.cargarVentas() }

    Column(modifier = Modifier.fillMaxSize()) {
        // 1. Manejo de Errores
        errorMessage?.let {
            ErrorCard(message = it, onRetry = { vm.cargarVentas() })
        }

        // 2. Resumen (Solo si hay ventas)
        if (ventas.isNotEmpty()) {
            SalesSummaryCard(ventas = ventas)
        }

        // 3. Área de contenido dinámico
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading && ventas.isEmpty() -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                ventas.isEmpty() && !isLoading -> {
                    // Usamos el componente genérico en lugar de la función local
                    EmptyState(
                        icon = "📈",
                        title = "No hay ventas registradas",
                        description = "Las ventas realizadas aparecerán en este historial.",
                        onAction = { vm.cargarVentas() },
                        actionLabel = "Actualizar"
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                "Historial Reciente",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        items(ventas) { venta ->
                            VentaItem(venta = venta)
                        }
                    }
                }
            }
        }
    }
}