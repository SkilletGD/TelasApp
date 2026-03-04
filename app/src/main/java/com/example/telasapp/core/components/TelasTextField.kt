package com.example.telasapp.core.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun TelasTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: @Composable (() -> Unit)? = null,
    enabled: Boolean = true, // <--- AGREGAMOS ESTO
    trailingIcon: @Composable (() -> Unit)? = null, // <--- AGREGADO
    visualTransformation: VisualTransformation = VisualTransformation.None, // <--- AGREGADO
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        enabled = enabled, // <--- LO PASAMOS AL TEXTFIELD INTERNO
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(text = error, color = Color.Red)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        prefix = prefix,
        trailingIcon = trailingIcon, // <--- PASAR AL INTERNO
        visualTransformation = visualTransformation, // <--- PASAR AL INTERNO
        singleLine = true
    )
}