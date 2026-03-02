package com.example.telasapp.features.detail.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.telasapp.data.models.DetalleRollo
import com.example.telasapp.navigation.Screen

@Composable
fun DetalleRolloItem(
    detalle: DetalleRollo
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Rollo #${detalle.numero_rollo}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Original: ${detalle.metros_iniciales}m",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${detalle.metros_restantes}m",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (detalle.metros_restantes > 0) MaterialTheme.colorScheme.primary else Color.Red
                )

                // Badge de estado interno del rollo
                Surface(
                    color = when (detalle.estado) {
                        "Completo" -> Color(0xFFE8F5E8)
                        "Agotado" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFFFF3E0)
                    },
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = detalle.estado,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when (detalle.estado) {
                            "Completo" -> Color(0xFF2E7D32)
                            "Agotado" -> Color(0xFFC62828)
                            else -> Color(0xFFEF6C00)
                        }
                    )
                }
            }
        }
    }
}