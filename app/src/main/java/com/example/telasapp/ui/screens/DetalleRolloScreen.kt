package com.example.telasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.data.models.Rollo  // ¡IMPORTACIÓN IMPORTANTE!
import com.example.telasapp.ui.viewmodel.InventarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRolloScreen(
    navController: NavController,
    rolloId: Int?,
    vm: InventarioViewModel
) {
    // Obtener la lista de rollos - FORMA CORRECTA
    val rollos by vm.rollos.collectAsState()

    // Buscar el rollo por ID
    val rolloOriginal = rollos.firstOrNull { it.id == rolloId }

    // Estados para los campos editables
    var tipoTela by remember { mutableStateOf(rolloOriginal?.tipo_tela ?: "") }
    var color by remember { mutableStateOf(rolloOriginal?.color ?: "") }
    var codigo by remember { mutableStateOf(rolloOriginal?.codigo ?: "") }

    // Estado para el diálogo de confirmación
    var showDeleteDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Dialogo de confirmación para eliminar
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
                                    snackbarHostState.showSnackbar("Rollo eliminado correctamente")
                                    navController.popBackStack()
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error al eliminar el rollo")
                            }
                        }
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (rolloOriginal != null) "Editar Rollo #${rolloOriginal.id}"
                        else "Rollo no encontrado"
                    )
                },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Rollo no encontrado", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("ID: $rolloId")
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Volver al Inventario")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Información del ID
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ID del Rollo:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            rolloOriginal.id.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666)
                        )
                    }
                }

                // Campos editables
                OutlinedTextField(
                    value = tipoTela,
                    onValueChange = { tipoTela = it },
                    label = { Text("Tipo de Tela") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Información de solo lectura
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Información del rollo:", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cantidad Total: ${rolloOriginal.cantidad_total}")
                        Text("Cantidad Restante: ${rolloOriginal.cantidad_restante}")
                        Text("Estado: ${rolloOriginal.estado}")
                        Text("Registrado por: ${rolloOriginal.registrado_por}")
                        rolloOriginal.proveedor?.let { proveedor ->
                            Text("Proveedor: $proveedor")
                        }
                        rolloOriginal.fecha_compra?.let { fecha ->
                            Text("Fecha de compra: $fecha")
                        }
                    }
                }

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Botón ELIMINAR
                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F)
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Eliminar")
                    }

                    // Botón GUARDAR
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    // Crear rollo actualizado
                                    val rolloActualizado = rolloOriginal.copy(
                                        tipo_tela = tipoTela,
                                        color = color,
                                        codigo = codigo
                                    )
                                    vm.actualizarRollo(rolloActualizado)
                                    snackbarHostState.showSnackbar("Rollo actualizado correctamente")
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error al actualizar: ${e.message}")
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = "Guardar",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar")
                    }
                }
            }
        }
    }
}