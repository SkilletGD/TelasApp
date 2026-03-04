package com.example.telasapp.features.auth.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.telasapp.core.components.TelasTextField
import com.example.telasapp.features.auth.viewmodel.AuthConfirmViewModel

@Composable
fun AuthConfirmDialog(
    email: String,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    authConfirmVm: AuthConfirmViewModel
) {
    // Estado local para ver/ocultar contraseña
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Identidad", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ingresa tu contraseña para autorizar esta operación.")

                TelasTextField(
                    value = authConfirmVm.password,
                    onValueChange = { authConfirmVm.password = it },
                    label = "Contraseña",
                    error = authConfirmVm.error,
                    keyboardType = KeyboardType.Password,
                    enabled = !authConfirmVm.isLoading,
                    // LÓGICA VISUAL
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    // ICONO DEL OJITO
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { authConfirmVm.confirmar(email, onSuccess) },
                enabled = !authConfirmVm.isLoading
            ) {
                if (authConfirmVm.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White)
                else Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}