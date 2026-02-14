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
import com.example.telasapp.features.inventory.ui.components.ErrorCard
import com.example.telasapp.features.inventory.ui.components.FilterPanel
import com.example.telasapp.features.inventory.ui.components.RolloItem
import com.example.telasapp.features.inventory.ui.components.SearchBar
import com.example.telasapp.features.inventory.ui.components.SearchSuggestions
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

    // Estados de UI
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedEstado by remember { mutableStateOf<String?>(null) }
    var minMetros by remember { mutableStateOf("") }
    var maxMetros by remember { mutableStateOf("") }

    // --- LOGICA DE DATOS ---
    LaunchedEffect(Unit) {
        vm.eventos.collect { snackbarHostState.showSnackbar(it) }
    }
    LaunchedEffect(Unit) { vm.cargarRollos() }

    // --- FILTRADO (Separado visualmente del diseño) ---
    val filteredRollos = remember(rollos, searchQuery, selectedEstado, minMetros, maxMetros) {
        rollos.filter { r ->
            val matchesSearch = searchQuery.isBlank() || listOf(r.tipo_tela, r.color, r.codigo).any {
                it.contains(searchQuery, ignoreCase = true)
            }
            val matchesEstado = selectedEstado == null || r.estado == selectedEstado
            val actual = r.cantidad_restante.toDoubleOrNull() ?: 0.0
            val inRange = actual >= (minMetros.toDoubleOrNull() ?: 0.0) &&
                    actual <= (maxMetros.toDoubleOrNull() ?: Double.MAX_VALUE)

            matchesSearch && matchesEstado && inRange
        }
    }

    val suggestions = remember(rollos, searchQuery) {
        if (searchQuery.length >= 2) {
            rollos.flatMap { listOf(it.tipo_tela, it.color, it.codigo) }
                .distinct()
                .filter { it.contains(searchQuery, ignoreCase = true) }
                .take(5)
        } else emptyList()
    }

    // --- DISEÑO ---
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            showFilters = showFilters,
            onFilterToggle = { showFilters = !showFilters }
        )

        SearchSuggestions(suggestions) { searchQuery = it }

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

        // Listado y estados
        Box(modifier = Modifier.fillMaxSize()) {
            if (errorMessage != null) {
                ErrorCard(message = errorMessage!!, onRetry = { vm.cargarRollos() })
            } else if (isLoading && rollos.isEmpty()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else if (filteredRollos.isEmpty()) {
                Text("No hay resultados", Modifier.align(Alignment.Center), color = Color.Gray)
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
}