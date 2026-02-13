package com.example.telasapp.features.inventory.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.data.models.Rollo
import com.example.telasapp.ui.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(navController: NavController, vm: InventarioViewModel, snackbarHostState: SnackbarHostState) {
    val rollos by vm.rollos.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()

    // Estados avanzados para búsqueda y filtros
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedEstado by remember { mutableStateOf<String?>(null) }
    var minMetros by remember { mutableStateOf("") }
    var maxMetros by remember { mutableStateOf("") }

    val estados = listOf("Disponible", "Agotado", "Vendido")

    // Búsqueda inteligente con múltiples criterios
    val filteredRollos = remember(rollos, searchQuery, selectedEstado, minMetros, maxMetros) {
        rollos.filter { rollo ->
            val matchesSearch = searchQuery.isBlank() || listOf(
                rollo.tipo_tela, rollo.color, rollo.codigo, rollo.estado
            ).any { it.contains(searchQuery, ignoreCase = true) }

            val matchesEstado = selectedEstado == null || rollo.estado == selectedEstado

            val minMetrosValue = minMetros.toDoubleOrNull() ?: 0.0
            val maxMetrosValue = maxMetros.toDoubleOrNull() ?: Double.MAX_VALUE
            val rolloMetros = rollo.cantidad_restante.toDoubleOrNull() ?: 0.0
            val matchesMetros = rolloMetros in minMetrosValue..maxMetrosValue

            matchesSearch && matchesEstado && matchesMetros
        }
    }

    // Sugerencias de búsqueda
    val searchSuggestions = remember(rollos, searchQuery) {
        if (searchQuery.length >= 2) {
            rollos.flatMap { listOf(it.tipo_tela, it.color, it.codigo) }
                .distinct()
                .filter { it.contains(searchQuery, ignoreCase = true) }
                .take(5)
        } else emptyList()
    }

    LaunchedEffect(Unit) {
        vm.cargarRollos()
    }

    // CONTENIDO PRINCIPAL (Sin Scaffold interno)
    Column(modifier = Modifier.fillMaxSize()) {

        // 1. BARRA DE BÚSQUEDA Y BOTÓN DE FILTROS
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, "Buscar", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Buscar telas, colores...") },
                        singleLine = true,
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, "Limpiar")
                                }
                            }
                        }
                    )
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            Icons.Default.FilterList,
                            "Filtros",
                            tint = if (showFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (searchSuggestions.isNotEmpty()) {
                    HorizontalDivider()
                    searchSuggestions.forEach { suggestion ->
                        Text(
                            text = "• $suggestion",
                            modifier = Modifier.fillMaxWidth().clickable { searchQuery = suggestion }.padding(16.dp, 8.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // 2. PANEL DE FILTROS AVANZADOS
        if (showFilters) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Estado:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        estados.forEach { estado ->
                            FilterChip(
                                selected = selectedEstado == estado,
                                onClick = { selectedEstado = if (selectedEstado == estado) null else estado },
                                label = { Text(estado) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Rango de metros:", style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = minMetros,
                            onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) minMetros = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Mín") }
                        )
                        Text(" - ", modifier = Modifier.padding(horizontal = 8.dp))
                        OutlinedTextField(
                            value = maxMetros,
                            onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) maxMetros = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Máx") }
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = {
                            selectedEstado = null; minMetros = ""; maxMetros = ""; showFilters = false
                        }) { Text("Limpiar") }
                        Button(onClick = { showFilters = false }) { Text("Aplicar") }
                    }
                }
            }
        }

        // 3. MENSAJES DE ERROR O CARGA
        errorMessage?.let { message ->
            if (message.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("❌ Error de conexión", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                        Text(message, style = MaterialTheme.typography.bodySmall)
                        Button(onClick = { vm.cargarRollos() }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Text("Reintentar Conexión")
                        }
                    }
                }
            }
        }

        // 4. LISTA DE DATOS
        if (isLoading && rollos.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        } else if (filteredRollos.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("No hay resultados", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp) // Espacio para el FAB global
            ) {
                items(filteredRollos) { rollo ->
                    RolloItem(rollo = rollo, navController = navController)
                }
            }
        }
    }
}

@Composable
fun RolloItem(rollo: Rollo, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("detalleRollo/${rollo.id}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${rollo.tipo_tela} - ${rollo.color}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Código: ${rollo.codigo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${rollo.cantidad_restante}m disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Badge de estado
                Surface(
                    color = when (rollo.estado) {
                        "Disponible" -> Color(0xFFE8F5E8)
                        "Agotado" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFE3F2FD)
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = rollo.estado,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when (rollo.estado) {
                            "Disponible" -> Color(0xFF2E7D32)
                            "Agotado" -> Color(0xFFC62828)
                            else -> Color(0xFF1565C0)
                        }
                    )
                }
            }

            // --- BOTÓN DE VENTA (Aparece solo si está disponible) ---
            if (rollo.estado == "Disponible" && (rollo.cantidad_restante.toDoubleOrNull() ?: 0.0) > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { navController.navigate("venta/${rollo.id}") },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("🛒 Registrar Venta")
                }
            }
        }
    }
}