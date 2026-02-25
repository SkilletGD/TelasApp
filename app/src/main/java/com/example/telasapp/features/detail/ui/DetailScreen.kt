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

    // 2. Verificamos si es ADMIN
    val authState by authVm.authState.collectAsState()
    val isAdmin = (authState as? AuthState.Success)?.user?.role == UserRole.ADMIN

    var tipoTela by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.tipo_tela ?: "") }
    var color by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.color ?: "") }
    var codigo by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.codigo ?: "") }

    LaunchedEffect(rolloId) { rolloId?.let { detailVm.cargarRollo(it) } }
    LaunchedEffect(Unit) { detailVm.eventos.collect { snackbarHostState.showSnackbar(it) } }

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
            RolloForm(
                tipoTela = tipoTela,
                onTipoTelaChange = { tipoTela = it },
                color = color,
                onColorChange = { color = it },
                codigo = codigo,
                onCodigoChange = { codigo = it },
                enabled = isAdmin
            )

            ShareActionsRow(
                onPrint = { detailVm.imprimirEtiqueta(context, rollo) },
                onShare = { detailVm.compartirEtiqueta(context, rollo) }
            )

            Spacer(Modifier.weight(1f))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(
                    onClick = { showDeleteDialog = true },
                    enabled = isAdmin
                ) {
                    Icon(Icons.Default.Delete, "Borrar", tint = Color.Red)
                }
                Button(
                    onClick = {
                        val updated = rollo.copy(tipo_tela = tipoTela, color = color, codigo = codigo)
                        detailVm.actualizarRollo(updated) {
                            invVm.cargarRollos()
                            navController.popBackStack()
                        }
                    },
                    enabled = isAdmin,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Save, null); Spacer(Modifier.width(8.dp)); Text("Guardar")
                }
            }
        }
    }

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