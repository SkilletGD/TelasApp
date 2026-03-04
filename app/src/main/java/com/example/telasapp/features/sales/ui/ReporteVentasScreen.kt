package com.example.telasapp.features.sales.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.telasapp.core.components.EmptyState // Importamos el genérico
import com.example.telasapp.features.inventory.ui.components.ErrorCard
import com.example.telasapp.features.sales.ui.components.ErrorCardSales
import com.example.telasapp.features.sales.ui.components.SalesSummaryCard
import com.example.telasapp.features.sales.ui.components.VentaItem
import com.example.telasapp.features.sales.viewmodel.SalesViewModel

@OptIn(ExperimentalMaterial3Api::class) // Para PullToRefresh
@Composable
fun ReporteVentasScreen(vm: SalesViewModel) {
    val ventas by vm.ventas.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()

    // Carga inicial
    LaunchedEffect(Unit) { vm.cargarVentas() }

    Scaffold(
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Manejo de Errores (Mejorado)
            errorMessage?.let {
                ErrorCardSales(
                    message = it,
                    onRetry = { vm.cargarVentas() },
                    modifier = Modifier.padding(16.dp)
                )
            }

            // 2. Resumen General (Muestra datos acumulados)
            if (ventas.isNotEmpty()) {
                SalesSummaryCard(ventas = ventas)
            }

            // 3. Contenido Principal
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading && ventas.isEmpty()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                } else if (ventas.isEmpty() && !isLoading) {
                    EmptyState(
                        icon = "📊", // Icono de gráfica para reportes
                        title = "Sin ventas aún",
                        description = "Aquí aparecerá el historial de lo que se corte de los lotes.",
                        onAction = { vm.cargarVentas() },
                        actionLabel = "Reintentar"
                    )
                } else {
                    // Lista de ventas con separador de sección
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Movimientos Recientes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                // Badge que indica el total de ítems
                                Badge { Text("${ventas.size}") }
                            }
                        }

                        items(ventas) { venta ->
                            VentaItem(venta = venta)
                        }

                        // Espacio extra al final para que el último item no quede tapado
                        item { Spacer(modifier = Modifier.height(50.dp)) }
                    }
                }
            }
        }
    }
}