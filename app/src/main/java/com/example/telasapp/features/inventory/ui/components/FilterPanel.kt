package com.example.telasapp.features.inventory.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPanel(
    selectedEstado: String?,
    onEstadoSelect: (String?) -> Unit,
    minMetros: String,
    onMinMetrosChange: (String) -> Unit,
    maxMetros: String,
    onMaxMetrosChange: (String) -> Unit,
    onClear: () -> Unit,
    onApply: () -> Unit
) {
    val estados = listOf("Disponible", "Agotado", "Vendido")

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Estado:", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                estados.forEach { estado ->
                    FilterChip(
                        selected = selectedEstado == estado,
                        onClick = { onEstadoSelect(if (selectedEstado == estado) null else estado) },
                        label = { Text(estado) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Rango de metros:", style = MaterialTheme.typography.labelMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = minMetros,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) onMinMetrosChange(it) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Mín") }
                )
                Text(" - ", modifier = Modifier.padding(horizontal = 8.dp))
                OutlinedTextField(
                    value = maxMetros,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) onMaxMetrosChange(it) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Máx") }
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onClear) { Text("Limpiar") }
                Button(onClick = onApply) { Text("Aplicar") }
            }
        }
    }
}