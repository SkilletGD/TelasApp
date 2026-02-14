package com.example.telasapp.core.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun TelasTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null, // Por defecto es null
    keyboardType: KeyboardType = KeyboardType.Text, // Por defecto texto normal
    prefix: @Composable (() -> Unit)? = null, // Opcional (para el "m" de metros)
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(text = error, color = Color.Red)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        prefix = prefix,
        singleLine = true
    )
}