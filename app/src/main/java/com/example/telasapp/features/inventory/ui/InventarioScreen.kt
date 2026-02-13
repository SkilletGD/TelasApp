package com.example.telasapp.features.inventory.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.features.inventory.ui.components.FilterPanel
import com.example.telasapp.features.inventory.ui.components.RolloItem
import com.example.telasapp.features.inventory.ui.components.SearchBar
import com.example.telasapp.features.inventory.viewmodel.InventarioViewModel

@Composable
fun InventarioScreen(
    navController: NavController,
    vm: InventarioViewModel,
    snackbarHostState: SnackbarHostState
) {
    val rollos by vm.rollos.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()

    // Estados de UI (Búsqueda y Filtros)
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedEstado by remember { mutableStateOf<String?>(null) }
    var minMetros by remember { mutableStateOf("") }
    var maxMetros by remember { mutableStateOf("") }

    // --- LÓGICA DE EVENTOS (SNACKBAR) ---
    LaunchedEffect(Unit) {
        vm.eventos.collect { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
        }
    }

    LaunchedEffect(Unit) {
        vm.cargarRollos()
    }

    // --- LÓGICA DE FILTRADO (Se queda en la View por ser estado efímero) ---
    val filteredRollos = remember(rollos, searchQuery, selectedEstado, minMetros, maxMetros) {
        rollos.filter { rollo ->
            val matchesSearch = searchQuery.isBlank() || listOf(
                rollo.tipo_tela, rollo.color, rollo.codigo, rollo.estado
            ).any { it.contains(searchQuery, ignoreCase = true) }

            val matchesEstado = selectedEstado == null || rollo.estado == selectedEstado

            val minVal = minMetros.toDoubleOrNull() ?: 0.0
            val maxVal = maxMetros.toDoubleOrNull() ?: Double.MAX_VALUE
            val actualMetros = rollo.cantidad_restante.toDoubleOrNull() ?: 0.0

            matchesSearch && matchesEstado && (actualMetros in minVal..maxVal)
        }
    }

    val searchSuggestions = remember(rollos, searchQuery) {
        if (searchQuery.length >= 2) {
            rollos.flatMap { listOf(it.tipo_tela, it.color, it.codigo) }
                .distinct()
                .filter { it.contains(searchQuery, ignoreCase = true) }
                .take(5)
        } else emptyList()
    }

    // --- DISEÑO DE LA PANTALLA ---
    Column(modifier = Modifier.fillMaxSize()) {

        // 1. BARRA DE BÚSQUEDA (Componente extraído)
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            showFilters = showFilters,
            onFilterToggle = { showFilters = !showFilters }
        )

        // Sugerencias rápidas (Inline por ser muy simples)
        if (searchSuggestions.isNotEmpty()) {
            searchSuggestions.forEach { suggestion ->
                Text(
                    text = "• $suggestion",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { searchQuery = suggestion }
                        .padding(horizontal = 32.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 2. PANEL DE FILTROS (Componente extraído)
        if (showFilters) {
            FilterPanel(
                selectedEstado = selectedEstado,
                onEstadoSelect = { selectedEstado = it },
                minMetros = minMetros,
                onMinMetrosChange = { minMetros = it },
                maxMetros = maxMetros,
                onMaxMetrosChange = { maxMetros = it },
                onClear = {
                    selectedEstado = null; minMetros = ""; maxMetros = ""; searchQuery = ""; showFilters = false
                },
                onApply = { showFilters = false }
            )
        }

        // 3. ESTADOS DE CARGA Y ERROR
        if (errorMessage != null) {
            ErrorCard(message = errorMessage!!, onRetry = { vm.cargarRollos() })
        }

        // 4. LISTADO (Componente extraído)
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
                contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
            ) {
                items(filteredRollos) { rollo ->
                    RolloItem(
                        rollo = rollo,
                        onVentaClick = { id -> navController.navigate("venta/$id") },
                        onDetailClick = { id -> navController.navigate("detalleRollo/$id") }
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorCard(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("❌ Error de conexión", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            Text(message, style = MaterialTheme.typography.bodySmall)
            Button(onClick = onRetry, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text("Reintentar Conexión")
            }
        }
    }
}