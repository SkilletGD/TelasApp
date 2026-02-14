package com.example.telasapp.features.detail.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RolloForm(
    tipoTela: String,
    onTipoTelaChange: (String) -> Unit,
    color: String,
    onColorChange: (String) -> Unit,
    codigo: String,
    onCodigoChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = tipoTela,
            onValueChange = onTipoTelaChange,
            label = { Text("Tipo de Tela") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = color,
            onValueChange = onColorChange,
            label = { Text("Color") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = codigo,
            onValueChange = onCodigoChange,
            label = { Text("Código") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}