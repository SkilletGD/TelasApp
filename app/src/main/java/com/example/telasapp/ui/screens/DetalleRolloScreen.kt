package com.example.telasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Print    // NUEVA
import androidx.compose.material.icons.filled.Share    // NUEVA
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext       // IMPORTANTE: Para la impresión
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.data.models.Rollo
import com.example.telasapp.ui.viewmodel.InventarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRolloScreen(
    navController: NavController,
    rolloId: Int?,
    vm: InventarioViewModel
) {
    val context = LocalContext.current // Necesario para generar y compartir el PDF
    val rollos by vm.rollos.collectAsState()
    val rolloOriginal = rollos.firstOrNull { it.id == rolloId }

    var tipoTela by remember { mutableStateOf(rolloOriginal?.tipo_tela ?: "") }
    var color by remember { mutableStateOf(rolloOriginal?.color ?: "") }
    var codigo by remember { mutableStateOf(rolloOriginal?.codigo ?: "") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Dialogo de confirmación (Se mantiene igual)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (rolloOriginal != null) "Editar Rollo #${rolloOriginal.id}" else "Error") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("← Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (rolloOriginal == null) {
            // ... (Vista de error)
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Permite scroll si la pantalla es chica
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

                // --- NUEVA SECCIÓN: EXPORTACIÓN INDUSTRIAL (B&W) ---
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
                    // Botón Imprimir (Blanco y Negro / Industrial)
                    Button(
                        onClick = { vm.imprimirEtiqueta(context, rolloOriginal) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black) // Negro para representar B&W
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Imprimir")
                    }

                    // Botón Compartir
                    OutlinedButton(
                        onClick = { vm.compartirEtiqueta(context, rolloOriginal) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
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
                        Text("Cantidad actual: ${rolloOriginal.cantidad_restante}m / ${rolloOriginal.cantidad_total}m")
                        Text("Estado: ${rolloOriginal.estado}")
                    }
                }

                // --- BOTONES DE ACCIÓN FINAL ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(0.4f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val actualizado = rolloOriginal.copy(
                                        tipo_tela = tipoTela, color = color, codigo = codigo
                                    )
                                    vm.actualizarRollo(actualizado)
                                    snackbarHostState.showSnackbar("Guardado")
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error")
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }
}