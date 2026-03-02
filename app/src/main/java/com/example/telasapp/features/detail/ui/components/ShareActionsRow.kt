package com.example.telasapp.features.detail.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShareActionsRow(
    onPrint: () -> Unit,
    onShare: () -> Unit,
    enabled: Boolean = true // <-- AGREGA ESTO
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onPrint,
            modifier = Modifier.weight(1f),
            enabled = enabled // Lo aplicamos al botón
        ) {
            Icon(Icons.Default.Print, null)
            Spacer(Modifier.width(8.dp))
            Text("Imprimir")
        }

        OutlinedButton(
            onClick = onShare,
            modifier = Modifier.weight(1f),
            enabled = enabled // Lo aplicamos al botón
        ) {
            Icon(Icons.Default.Share, null)
            Spacer(Modifier.width(8.dp))
            Text("Compartir")
        }
    }
}