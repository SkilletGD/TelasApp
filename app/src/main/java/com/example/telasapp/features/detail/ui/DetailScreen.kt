package com.example.telasapp.features.detail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.features.detail.viewmodel.DetailViewModel
import com.example.telasapp.features.inventory.viewmodel.InventarioViewModel

@Composable
fun DetailScreen(
    navController: NavController,
    rolloId: Int?,
    detailVm: DetailViewModel,
    invVm: InventarioViewModel, // Lo usamos solo para extraer el dato de la lista global
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val rolloOriginal by detailVm.rollo.collectAsState()

    // Estados de edición local
    var tipoTela by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.tipo_tela ?: "") }
    var color by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.color ?: "") }
    var codigo by remember(rolloOriginal) { mutableStateOf(rolloOriginal?.codigo ?: "") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(rolloId) {
        rolloId?.let { id ->
            detailVm.cargarRollo(id)
        }
    }

    // Escuchar eventos (mensajes)
    LaunchedEffect(Unit) {
        detailVm.eventos.collect { snackbarHostState.showSnackbar(it) }
    }

    // Verificamos si aún está cargando (rolloOriginal es null al inicio)
    if (rolloOriginal == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            // Mientras carga mostramos un indicador o el texto de "Cargando..."
            CircularProgressIndicator()
        }
    } else {
        // Usamos una variable local no nula para facilitar el código
        val rollo = rolloOriginal!!
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = tipoTela, onValueChange = { tipoTela = it }, label = { Text("Tipo de Tela") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = codigo, onValueChange = { codigo = it }, label = { Text("Código") }, modifier = Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { detailVm.imprimirEtiqueta(context, rollo) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                    Icon(Icons.Default.Print, null); Spacer(Modifier.width(8.dp)); Text("Imprimir")
                }
                OutlinedButton(onClick = { detailVm.compartirEtiqueta(context, rollo) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Share, null); Spacer(Modifier.width(8.dp)); Text("Compartir")
                }
            }

            Spacer(Modifier.weight(1f))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, "Borrar", tint = Color.Red)
                }
                Button(
                    onClick = {
                        // Creamos la copia con los datos nuevos de los TextField
                        val updated = rollo.copy(tipo_tela = tipoTela, color = color, codigo = codigo)
                        detailVm.actualizarRollo(updated) {
                            invVm.cargarRollos()
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Save, null); Spacer(Modifier.width(8.dp)); Text("Guardar Cambios")
                }
            }
        }
    }

    // El diálogo de borrado se mantiene aquí o en un componente
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar rollo?") },
            confirmButton = {
                TextButton(onClick = {
                    // AQUÍ ESTABA EL ERROR: Usamos 'let' para pasar el ID seguro
                    rolloId?.let { id ->
                        detailVm.eliminarRollo(id) {
                            invVm.cargarRollos()
                            navController.popBackStack()
                        }
                    }
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }
}