package com.example.telasapp.features.inventory.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.data.models.Rollo

@Composable
fun RolloItem(
    rollo: Rollo,
    onVentaClick: (Int) -> Unit,
    onDetailClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailClick(rollo.id ?: 0) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${rollo.tipo_tela} - ${rollo.color}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Código: ${rollo.codigo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${rollo.cantidad_restante}m disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Badge de estado
                Surface(
                    color = when (rollo.estado) {
                        "Disponible" -> Color(0xFFE8F5E8)
                        "Agotado" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFE3F2FD)
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = rollo.estado,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when (rollo.estado) {
                            "Disponible" -> Color(0xFF2E7D32)
                            "Agotado" -> Color(0xFFC62828)
                            else -> Color(0xFF1565C0)
                        }
                    )
                }
            }

            // --- BOTÓN DE VENTA (Aparece solo si está disponible) ---
            if (rollo.estado == "Disponible" && (rollo.cantidad_restante.toDoubleOrNull() ?: 0.0) > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onVentaClick(rollo.id ?: 0) },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("🛒 Registrar Venta")
                }
            }
        }
    }
}