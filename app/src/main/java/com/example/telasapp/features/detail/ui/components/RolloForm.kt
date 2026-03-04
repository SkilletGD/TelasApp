package com.example.telasapp.features.detail.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.telasapp.core.components.TelasTextField

@Composable
fun RolloForm(
    // Datos básicos
    tipoTela: String,
    onTipoTelaChange: (String) -> Unit,
    color: String,
    onColorChange: (String) -> Unit,
    codigo: String,
    onCodigoChange: (String) -> Unit,

    // Precio (Editable)
    precio: String,
    onPrecioChange: (String) -> Unit,

    // Existencias (Editable)
    rollosDisponibles: String,        // Ahora es String para que el teclado funcione fluido
    onRollosChange: (String) -> Unit,

    // Info del sistema (Solo lectura)
    metrosTotales: Double,

    imagenUrl: String, // <--- NUEVO
    onImagenUrlChange: (String) -> Unit,

    enabled: Boolean = true
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // --- 1. IDENTIFICACIÓN ---
        OutlinedTextField(
            value = tipoTela,
            onValueChange = onTipoTelaChange,
            label = { Text("Tipo de Tela") },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = color,
                onValueChange = onColorChange,
                label = { Text("Color") },
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = codigo,
                onValueChange = onCodigoChange,
                label = { Text("Código Lote") },
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
        }

        TelasTextField(
            value = imagenUrl,
            onValueChange = onImagenUrlChange,
            label = "URL de la Imagen",
            enabled = enabled
        )

        // --- 2. PRECIO Y CANTIDAD (EDITABLES) ---
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Precio
            OutlinedTextField(
                value = precio,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) onPrecioChange(it) },
                label = { Text("Precio x Metro") },
                prefix = { Text("$ ") },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            // Cantidad de Rollos
            OutlinedTextField(
                value = rollosDisponibles,
                onValueChange = { if (it.all { char -> char.isDigit() }) onRollosChange(it) },
                label = { Text("Cant. Rollos") },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        // --- 3. RESUMEN DE STOCK (SOLO LECTURA) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Column {
                    Text("Stock Total Calculado", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "${metrosTotales} metros",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}