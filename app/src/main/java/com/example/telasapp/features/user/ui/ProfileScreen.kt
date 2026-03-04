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
import com.example.telasapp.data.models.UserRole
import com.example.telasapp.data.preferences.TokenManager
import com.example.telasapp.features.auth.viewmodel.AuthViewModel
import com.example.telasapp.features.auth.viewmodel.AuthState
import com.example.telasapp.navigation.Screen

@Composable
fun ProfileScreen(
    navController: NavController,
    authVm: AuthViewModel,
    tokenManager: TokenManager
) {

    val email by tokenManager.userEmail.collectAsState(initial = "Cargando...")
    val role by tokenManager.userRole.collectAsState(initial = "Cargando...")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // --- AVATAR CON ESTILO ---
        Surface(
            modifier = Modifier.size(120.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(70.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Perfil de Usuario",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // --- CHIP DE ROL ---
        val roleColor = if (role == "ADMIN") Color(0xFFFFC107) else MaterialTheme.colorScheme.secondary
        AssistChip(
            onClick = { },
            label = { Text(role ?: "N/A", fontWeight = FontWeight.Bold) },
            colors = AssistChipDefaults.assistChipColors(
                labelColor = roleColor,
                // Si no usas icono, puedes omitir leadingIconContentColor
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- TARJETA DE DATOS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ProfileDataItem(
                    label = "Correo Electrónico",
                    value = email ?: "N/A",
                    icon = Icons.Default.Person
                )

                // Divisor sutil entre items
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

                ProfileDataItem(
                    label = "Contraseña",
                    value = "••••••••",
                    icon = null // Podrías pasar otro icono si quieres
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- BOTÓN CERRAR SESIÓN ---
        OutlinedButton(
            onClick = {
                authVm.logout(tokenManager)
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("CERRAR SESIÓN", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileDataItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}