package com.example.telasapp.features.detail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.data.models.UserRole
import com.example.telasapp.data.preferences.TokenManager
import com.example.telasapp.features.auth.ui.components.AuthConfirmDialog
import com.example.telasapp.features.auth.viewmodel.AuthConfirmViewModel
import com.example.telasapp.features.auth.viewmodel.AuthState
import com.example.telasapp.features.auth.viewmodel.AuthViewModel
import com.example.telasapp.features.detail.ui.components.* // Importamos los componentes
import com.example.telasapp.features.detail.viewmodel.DetailViewModel
import com.example.telasapp.features.inventory.viewmodel.InventarioViewModel

@Composable
fun DetailScreen(
    navController: NavController,
    rolloId: Int?,
    detailVm: DetailViewModel,
    invVm: InventarioViewModel,
    authVm: AuthViewModel,
    authConfirmVm: AuthConfirmViewModel,
    tokenManager: TokenManager,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    // 1. FUENTE DE VERDAD: Rol y Email desde DataStore
    val savedRole by tokenManager.userRole.collectAsState(initial = null)
    val userEmail by tokenManager.userEmail.collectAsState(initial = "")
    val isAdmin = savedRole == UserRole.ADMIN.name

    val rolloOriginal by detailVm.rollo.collectAsState()

    // Estados para controlar los diálogos
    var showDeleteDialog by remember { mutableStateOf(false) }
    var accionSeguridad by remember { mutableStateOf<String?>(null) } // "GUARDAR" o "ELIMINAR"

    // --- ESTADOS EDITABLES ---
    var tipoTela by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.tipo_tela ?: "") }
    var color by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.color ?: "") }
    var codigo by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.codigo ?: "") }
    var precio by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.precio?.toString() ?: "") }
    var imagenUrl by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.imagen_url ?: "") }

    var rollosEditables by remember(rolloOriginal) {
        mutableStateOf((rolloOriginal?.rollos_disponibles ?: 0).toString())
    }

    LaunchedEffect(rolloId) { rolloId?.let { detailVm.cargarRollo(it) } }

    LaunchedEffect(Unit) {
        detailVm.eventos.collect { snackbarHostState.showSnackbar(it) }
    }

    // --- DIÁLOGO DE SEGURIDAD (EL CUADRITO) ---
    if (accionSeguridad != null) {
        AuthConfirmDialog(
            email = userEmail ?: "",
            onDismiss = {
                accionSeguridad = null
                authConfirmVm.reset()
            },
            onSuccess = {
                val tempAccion = accionSeguridad
                accionSeguridad = null // Cerramos antes de ejecutar

                if (tempAccion == "GUARDAR") {
                    val updated = rolloOriginal?.copy(
                        tipo_tela = tipoTela,
                        color = color,
                        codigo = codigo,
                        precio = precio.toDoubleOrNull() ?: (rolloOriginal?.precio ?: 0.0),
                        cantidad_rollos = rollosEditables.toIntOrNull() ?: (rolloOriginal?.cantidad_rollos ?: 0),
                        imagen_url = imagenUrl // <--- AGREGADO
                    )
                    updated?.let {
                        detailVm.actualizarRollo(it) {
                            invVm.cargarRollos()
                            navController.popBackStack()
                        }
                    }
                } else if (tempAccion == "ELIMINAR") {
                    rolloId?.let { id ->
                        detailVm.eliminarRollo(id) {
                            invVm.cargarRollos()
                            navController.popBackStack()
                        }
                    }
                }
            },
            authConfirmVm = authConfirmVm
        )
    }

    if (rolloOriginal == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val rollo = rolloOriginal!!
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de Resumen
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Resumen de Inventario", style = MaterialTheme.typography.labelLarge)
                    Text(
                        "${rollo.metros_reales_restantes ?: 0.0}m disponibles",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text("${rollo.rollos_disponibles ?: 0} rollos activos de ${rollo.cantidad_rollos}")
                }
            }

            RolloForm(
                tipoTela = tipoTela,
                onTipoTelaChange = { tipoTela = it },
                color = color,
                onColorChange = { color = it },
                codigo = codigo,
                onCodigoChange = { codigo = it },
                precio = precio,
                onPrecioChange = { precio = it },
                rollosDisponibles = rollosEditables,
                onRollosChange = { rollosEditables = it },
                metrosTotales = rollo.metros_reales_restantes ?: 0.0,
                imagenUrl = imagenUrl,
                onImagenUrlChange = { imagenUrl = it },
                enabled = isAdmin
            )

            if (!rollo.detalles_rollos.isNullOrEmpty()) {
                Text("Desglose del Lote", style = MaterialTheme.typography.titleSmall)
                rollo.detalles_rollos.forEach { detalle -> DetalleRolloItem(detalle) }
            }

            ShareActionsRow(
                onPrint = { detailVm.imprimirEtiqueta(context, rollo) },
                onShare = { detailVm.compartirEtiqueta(context, rollo) },
                enabled = true
            )

            Spacer(Modifier.height(24.dp))

            // --- BOTONES DE ACCIÓN ---
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(
                    onClick = { showDeleteDialog = true },
                    enabled = isAdmin
                ) {
                    Icon(Icons.Default.Delete, "Borrar", tint = if (isAdmin) Color.Red else Color.Gray)
                }

                Button(
                    onClick = { accionSeguridad = "GUARDAR" },
                    enabled = isAdmin,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Save, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar Cambios")
                }
            }
        }
    }

    // Diálogo de confirmación visual antes del de seguridad
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar Lote?") },
            text = { Text("Esta acción no se puede deshacer. Se requerirá tu contraseña de administrador.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    accionSeguridad = "ELIMINAR"
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}