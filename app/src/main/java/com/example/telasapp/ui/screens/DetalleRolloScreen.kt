package com.example.telasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.data.models.Rollo
import com.example.telasapp.ui.viewmodel.InventarioViewModel
import kotlinx.coroutines.launch

@Composable
fun DetalleRolloScreen(
    navController: NavController,
    rolloId: Int?,
    vm: InventarioViewModel,
    snackbarHostState: SnackbarHostState // Recibido del Scaffold Global
) {
    val context = LocalContext.current
    val rollos by vm.rollos.collectAsState(initial = emptyList())
    val rolloOriginal = rollos.firstOrNull { it.id == rolloId }

    // Estados para la edición
    var tipoTela by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.tipo_tela ?: "") }
    var color by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.color ?: "") }
    var codigo by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.codigo ?: "") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // 1. Lógica de Diálogo de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar rollo?") },
            text = { Text("Esta acción no se puede deshacer. ¿Estás seguro?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        scope.launch {
                            try {
                                rolloOriginal?.id?.let { id ->
                                    vm.eliminarRollo(id)
                                    navController.popBackStack()
                                    snackbarHostState.showSnackbar("Rollo eliminado")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error al eliminar")
                            }
                        }
                    }
                ) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // 2. CONTENIDO DE LA PANTALLA (Sin Scaffold propio)
    if (rolloOriginal == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando datos o el rollo no existe...")
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- CAMPOS EDITABLES ---
            OutlinedTextField(
                value = tipoTela, onValueChange = { tipoTela = it },
                label = { Text("Tipo de Tela") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = color, onValueChange = { color = it },
                label = { Text("Color") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = codigo, onValueChange = { codigo = it },
                label = { Text("Código") }, modifier = Modifier.fillMaxWidth()
            )

            // --- SECCIÓN: ETIQUETADO ---
            Text(
                text = "Etiquetado Industrial",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { vm.imprimirEtiqueta(context, rolloOriginal) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Icon(Icons.Default.Print, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Imprimir")
                }

                OutlinedButton(
                    onClick = { vm.compartirEtiqueta(context, rolloOriginal) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Compartir")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // --- INFO SOLO LECTURA ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cantidad: ${rolloOriginal.cantidad_restante}m / ${rolloOriginal.cantidad_total}m",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Estado: ${rolloOriginal.estado}",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- BOTONES DE ACCIÓN FINAL ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón Eliminar
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(0.4f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }

                // Botón Guardar
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val actualizado = rolloOriginal.copy(
                                    tipo_tela = tipoTela, color = color, codigo = codigo
                                )
                                vm.actualizarRollo(actualizado)
                                snackbarHostState.showSnackbar("Cambios guardados con éxito")
                                navController.popBackStack()
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error al guardar")
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar Cambios")
                }
            }
        }
    }
}