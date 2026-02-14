package com.example.telasapp.features.inventory.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ErrorCard(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("❌ Error de conexión", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            Text(message, style = MaterialTheme.typography.bodySmall)
            Button(onClick = onRetry, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text("Reintentar Conexión")
            }
        }
    }
}