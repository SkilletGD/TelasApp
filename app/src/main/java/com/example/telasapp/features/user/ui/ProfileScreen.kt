package com.example.telasapp.features.user.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.features.auth.viewmodel.AuthViewModel
import com.example.telasapp.features.auth.viewmodel.AuthState
import com.example.telasapp.navigation.Screen

@Composable
fun ProfileScreen(
    navController: NavController,
    authVm: AuthViewModel
) {
    val authState by authVm.authState.collectAsState()

    // Obtenemos los datos del estado actual
    val user = (authState as? AuthState.Success)?.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Información de Usuario",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Card con los datos
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileDataItem(label = "Correo Electrónico", value = user?.email ?: "N/A")
                ProfileDataItem(label = "Tipo de Usuario", value = user?.role?.name ?: "N/A")
                ProfileDataItem(label = "Contraseña", value = "********") // Por seguridad no se muestra la real
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón de Cerrar Sesión
        Button(
            onClick = {
                authVm.logout()
                // Al cerrar sesión, reiniciamos el grafo para ir al Login y borrar historial
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("CERRAR SESIÓN")
        }
    }
}

@Composable
fun ProfileDataItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}