package com.example.telasapp.features.sales.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.telasapp.data.models.Venta

@Composable
fun SalesSummaryCard(ventas: List<Venta>) {
    if (ventas.isEmpty()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Resumen General de Ventas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Total Tickets: ${ventas.size}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Vendedores: ${ventas.map { it.vendedor }.distinct().size}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    // NUEVO: Suma de dinero total (si el campo existe en el modelo)
                    val ingresosTotales = ventas.sumOf { it.total_venta ?: 0.0 }
                    if (ingresosTotales > 0) {
                        Text(
                            text = "Recaudado: $${"%.2f".format(ingresosTotales)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    // --- CORRECCIÓN AQUÍ: Usamos metros_vendidos ---
                    val totalMetros = ventas.sumOf { it.metros_vendidos }

                    Text("Metros Totales", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "${"%.2f".format(totalMetros)}m",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}