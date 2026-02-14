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
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Venta #${venta.id ?: "---"}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(text = "Rollo ID: ${venta.rollo_id}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) {
                    Text(
                        text = "${venta.cantidad_vendida}m",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Vendedor", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(venta.vendedor, style = MaterialTheme.typography.bodyMedium)
                }
                venta.cliente?.takeIf { it.isNotBlank() }?.let { cliente ->
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cliente", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(cliente, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}