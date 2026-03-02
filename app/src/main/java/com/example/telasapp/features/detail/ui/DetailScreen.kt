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
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val rolloOriginal by detailVm.rollo.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val authState by authVm.authState.collectAsState()
    val isAdmin = (authState as? AuthState.Success)?.user?.role == UserRole.ADMIN

    // --- ESTADOS EDITABLES ---
    // Usamos remember(rolloOriginal) para que si el objeto cambia (por un refresh), los campos se actualicen
    var tipoTela by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.tipo_tela ?: "") }
    var color by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.color ?: "") }
    var codigo by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.codigo ?: "") }
    var precio by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.precio?.toString() ?: "") }

    // IMPORTANTE: Esta es la variable que pasaremos al TextField de Rollos
    var rollosEditables by remember(rolloOriginal) {
        mutableStateOf((rolloOriginal?.rollos_disponibles ?: 0).toString())
    }

    LaunchedEffect(rolloId) { rolloId?.let { detailVm.cargarRollo(it) } }

    // Recolectar eventos para mostrar el Snackbar
    LaunchedEffect(Unit) {
        detailVm.eventos.collect { snackbarHostState.showSnackbar(it) }
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
            // Tarjeta de Resumen (Solo Lectura)
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Resumen de Inventario", style = MaterialTheme.typography.labelLarge)
                    Text(
                        "${rollo.metros_reales_restantes ?: 0.0}m disponibles en total",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text("${rollo.rollos_disponibles ?: 0} de ${rollo.cantidad_rollos} rollos activos")
                }
            }

            // --- LLAMADA CORREGIDA AL FORMULARIO ---
            RolloForm(
                tipoTela = tipoTela,
                onTipoTelaChange = { tipoTela = it },
                color = color,
                onColorChange = { color = it },
                codigo = codigo,
                onCodigoChange = { codigo = it },
                precio = precio,
                onPrecioChange = { precio = it },
                // Pasamos la versión String y el callback de cambio
                rollosDisponibles = rollosEditables,
                onRollosChange = { rollosEditables = it },
                // Solo lectura
                metrosTotales = rollo.metros_reales_restantes ?: 0.0,
                enabled = isAdmin
            )

            // Desglose (Opcional)
            if (!rollo.detalles_rollos.isNullOrEmpty()) {
                Text("Desglose del Lote", style = MaterialTheme.typography.titleSmall)
                rollo.detalles_rollos.forEach { detalle ->
                    DetalleRolloItem(detalle)
                }
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
                    Icon(Icons.Default.Delete, "Borrar", tint = Color.Red)
                }

                Button(
                    onClick = {
                        val updated = rollo.copy(
                            tipo_tela = tipoTela,
                            color = color,
                            codigo = codigo,
                            precio = precio.toDoubleOrNull() ?: rollo.precio,
                            // Si tu API permite actualizar la cantidad de rollos base:
                            cantidad_rollos = rollosEditables.toIntOrNull() ?: rollo.cantidad_rollos
                        )
                        detailVm.actualizarRollo(updated) {
                            invVm.cargarRollos() // Refrescar lista principal
                            navController.popBackStack()
                        }
                    },
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

    // Diálogo de eliminación (se mantiene igual)
    if (showDeleteDialog) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                rolloId?.let { id ->
                    detailVm.eliminarRollo(id) {
                        invVm.cargarRollos()
                        navController.popBackStack()
                    }
                }
            }
        )
    }
}