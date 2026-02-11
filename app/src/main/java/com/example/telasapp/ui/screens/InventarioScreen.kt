package com.example.telasapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.telasapp.ui.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(navController: NavController, vm: InventarioViewModel) {
    val rollos by vm.rollos.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()

    // Estados avanzados para búsqueda
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedEstado by remember { mutableStateOf<String?>(null) }
    var minMetros by remember { mutableStateOf("") }
    var maxMetros by remember { mutableStateOf("") }

    // Filtros disponibles
    val estados = listOf("Disponible", "Agotado", "Vendido")

    // Búsqueda inteligente con múltiples criterios
    val filteredRollos = remember(rollos, searchQuery, selectedEstado, minMetros, maxMetros) {
        rollos.filter { rollo ->
            val matchesSearch = searchQuery.isBlank() || listOf(
                rollo.tipo_tela,
                rollo.color,
                rollo.codigo,
                rollo.estado,
                rollo.cantidad_total,
                rollo.cantidad_restante
            ).any { it.contains(searchQuery, ignoreCase = true) }

            val matchesEstado = selectedEstado == null || rollo.estado == selectedEstado

            val minMetrosValue = minMetros.toDoubleOrNull() ?: 0.0
            val maxMetrosValue = maxMetros.toDoubleOrNull() ?: Double.MAX_VALUE
            val rolloMetros = rollo.cantidad_restante.toDoubleOrNull() ?: 0.0
            val matchesMetros = rolloMetros in minMetrosValue..maxMetrosValue

            matchesSearch && matchesEstado && matchesMetros
        }
    }

    // Sugerencias de búsqueda basadas en datos existentes
    val searchSuggestions = remember(rollos, searchQuery) {
        if (searchQuery.length >= 2) {
            val allTerms = rollos.flatMap { rollo ->
                listOf(
                    rollo.tipo_tela,
                    rollo.color,
                    rollo.codigo,
                    rollo.estado
                )
            }.distinct()

            allTerms.filter { it.contains(searchQuery, ignoreCase = true) }
                .take(5) // Máximo 5 sugerencias
        } else {
            emptyList()
        }
    }

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        vm.cargarRollos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Inventario de Telas")
                        if (isLoading) {
                            Spacer(modifier = Modifier.width(8.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                },
                actions = {
                    // Botón de Filtros
                    IconButton(
                        onClick = { showFilters = !showFilters }
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filtros",
                            tint = if (selectedEstado != null || minMetros.isNotBlank() || maxMetros.isNotBlank()) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    // Botón para Reportes de Ventas
                    IconButton(
                        onClick = { navController.navigate("reporteVentas") }
                    ) {
                        Text("📊", style = MaterialTheme.typography.bodyLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("nuevoRollo")
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar rollo")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Barra de búsqueda MEJORADA
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    // Campo de búsqueda principal
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Buscar telas, colores, códigos...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            )
                        )
                        if (searchQuery.isNotBlank()) {
                            IconButton(
                                onClick = { searchQuery = "" }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                    }

                    // Sugerencias de búsqueda
                    if (searchSuggestions.isNotEmpty()) {
                        Divider()
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                "Sugerencias:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                            searchSuggestions.forEach { suggestion ->
                                Text(
                                    text = "• $suggestion",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { searchQuery = suggestion }
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Panel de Filtros Avanzados
            if (showFilters) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Filtros Avanzados",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${filteredRollos.size} resultados",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Filtro por Estado
                        Text(
                            "Estado:",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            estados.forEach { estado ->
                                FilterChip(
                                    selected = selectedEstado == estado,
                                    onClick = {
                                        selectedEstado = if (selectedEstado == estado) null else estado
                                    },
                                    label = { Text(estado) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Filtro por Metros
                        Text(
                            "Metros disponibles:",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = minMetros,
                                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) minMetros = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Mín") },
                                singleLine = true
                            )
                            Text("a", modifier = Modifier.padding(horizontal = 4.dp))
                            OutlinedTextField(
                                value = maxMetros,
                                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) maxMetros = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Máx") },
                                singleLine = true
                            )
                        }

                        // Botones de acción de filtros
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    selectedEstado = null
                                    minMetros = ""
                                    maxMetros = ""
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text("Limpiar Filtros")
                            }
                            Button(
                                onClick = { showFilters = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Aplicar")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Contador de resultados MEJORADO
            if (searchQuery.isNotBlank() || selectedEstado != null || minMetros.isNotBlank() || maxMetros.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📊 ${filteredRollos.size} resultado(s)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        if (searchQuery.isNotBlank() || selectedEstado != null) {
                            Text(
                                text = buildString {
                                    if (searchQuery.isNotBlank()) append("\"$searchQuery\"")
                                    if (selectedEstado != null) {
                                        if (searchQuery.isNotBlank()) append(" • ")
                                        append("Estado: $selectedEstado")
                                    }
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ... (el resto de tu código se mantiene igual para estados de carga, error, etc.)
            // Mostrar mensaje de error si existe
            errorMessage?.let { message ->
                if (message.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("❌", style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Error de conexión",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFD32F2F)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { vm.clearError() }
                                ) {
                                    Text("✕", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD32F2F)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    vm.clearError()
                                    vm.cargarRollos()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Reintentar Conexión")
                            }
                        }
                    }
                }
            }

            // Estado de carga
            if (isLoading && rollos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Conectando con la base de datos...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            // Estado vacío (no hay datos)
            else if (filteredRollos.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            if (searchQuery.isNotBlank() || selectedEstado != null) "🔍" else "📦",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            if (searchQuery.isNotBlank() || selectedEstado != null)
                                "No se encontraron resultados"
                            else
                                "No hay rollos registrados",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            if (searchQuery.isNotBlank() || selectedEstado != null)
                                "Intenta ajustar los filtros o términos de búsqueda"
                            else
                                "El servidor puede tardar unos segundos en responder",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        if (searchQuery.isBlank() && selectedEstado == null) {
                            Button(
                                onClick = { vm.cargarRollos() }
                            ) {
                                Text("Reintentar Conexión")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { navController.navigate("nuevoRollo") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Text("Agregar Primer Rollo")
                            }
                        } else {
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedEstado = null
                                    minMetros = ""
                                    maxMetros = ""
                                }
                            ) {
                                Text("Limpiar Búsqueda")
                            }
                        }
                    }
                }
            }
            // Estado con datos
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredRollos) { rollo ->
                        RolloItem(rollo = rollo, navController = navController)
                    }

                    // Loading al final para cargar más datos (si aplica)
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// El composable RolloItem se mantiene igual
@Composable
fun RolloItem(rollo: com.example.telasapp.data.models.Rollo, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("detalleRollo/${rollo.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${rollo.tipo_tela} - ${rollo.color}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Código: ${rollo.codigo}", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "${rollo.cantidad_restante}m / ${rollo.cantidad_total}m disponibles",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Estado: ${rollo.estado}",
                color = when (rollo.estado) {
                    "Disponible" -> Color(0xFF2E7D32)
                    "Agotado" -> Color(0xFFD32F2F)
                    "Vendido" -> Color(0xFF1976D2)
                    else -> MaterialTheme.colorScheme.onSurface
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (rollo.estado == "Disponible" && rollo.cantidad_restante.toDoubleOrNull() ?: 0.0 > 0) {
                Button(
                    onClick = {
                        navController.navigate("venta/${rollo.id}")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Vender Metros")
                }
            }
        }
    }
}