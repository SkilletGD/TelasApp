package com.example.telasapp.features.sales.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.telasapp.data.models.Venta

@Composable
fun VentaItem(venta: Venta) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Venta #${venta.id ?: "---"}", fontWeight = FontWeight.Bold)
                    Text(text = "Rollo ID: ${venta.rollo_id}", style = MaterialTheme.typography.bodySmall)
                }
                    // NUEVO: Mostrar el código del lote si lo tienes en el modelo Venta
                    // Si no lo tienes, al menos aclara que es el ID del rollo físico
                    Text(
                        text = "ID Rollo Físico: ${venta.rollo_id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

            // --- CAMBIO AQUÍ: Usamos metros_vendidos ---
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) {
                Text(
                    text = "${venta.metros_vendidos}m",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Vendedor", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

                    // Lógica de respaldo para el vendedor
                    val nombreVendedor = when {
                        !venta.vendedor.isNullOrBlank() -> venta.vendedor
                        else -> "Sin asignar / Sistema" // <-- Este es tu manejador de error visual
                    }

                    Text(
                        text = nombreVendedor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (venta.vendedor.isNullOrBlank()) Color.Red.copy(alpha = 0.6f) else Color.Unspecified
                    )
                }

                // Cliente opcional
                val clienteVal = venta.cliente?.takeIf { it.isNotBlank() }
                if (clienteVal != null) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("Cliente", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(clienteVal, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            venta.total_venta?.let { total ->
                Text(
                    text = "Total: $${String.format("%.2f", total)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}