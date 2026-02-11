package com.example.telasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.ui.viewmodel.InventarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteVentasScreen(
    navController: NavController,
    vm: InventarioViewModel
) {
    val ventas by vm.ventas.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        vm.cargarVentas()
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📊 Reporte de Ventas")
                        if (isLoading) {
                            Spacer(modifier = Modifier.width(8.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón para actualizar
                    IconButton(
                        onClick = {
                            scope.launch {
                                vm.cargarVentas()
                                snackbarHostState.showSnackbar("Ventas actualizadas")
                            }
                        }
                    ) {
                        Text("🔄")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Mostrar mensaje de error si existe
            errorMessage?.let { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("❌ Error al cargar ventas", color = Color(0xFFD32F2F))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(message, color = Color(0xFFD32F2F))
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                vm.clearError()
                                vm.cargarVentas()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            // Resumen de ventas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Resumen General",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total de ventas: ${ventas.size}")
                    Text("Vendedores: ${ventas.map { it.vendedor }.distinct().size}")
                    Text("Rollos vendidos: ${ventas.map { it.rollo_id }.distinct().size}")
                    val totalMetros = ventas.sumOf { it.cantidad_vendida }
                    Text("Total metros vendidos: ${"%.2f".format(totalMetros)}m")
                }
            }

            // Estado de carga
            if (isLoading && ventas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando reporte de ventas...")
                    }
                }
            }
            // Estado vacío
            else if (ventas.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text("📈", style = MaterialTheme.typography.displayMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No hay ventas registradas",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Las ventas aparecerán aquí cuando se realicen",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            // Lista de ventas
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ventas) { venta ->
                        VentaItem(venta = venta)
                    }
                }
            }
        }
    }
}

@Composable
fun VentaItem(venta: com.example.telasapp.data.models.Venta) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header con información principal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Venta #${venta.id ?: "N/A"}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rollo ID: ${venta.rollo_id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${venta.cantidad_vendida}m",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información de la venta - SOLO PROPIEDADES QUE EXISTEN
            Column {
                Text("Vendedor: ${venta.vendedor}")

                venta.cliente?.let { cliente ->
                    if (cliente.isNotBlank()) {
                        Text("Cliente: $cliente")
                    }
                }

                // Nota: fecha_venta no existe en tu data class actual
                // Se agregaría automáticamente desde la base de datos
                Text("ID de Rollo: ${venta.rollo_id}")
            }
        }
    }
}