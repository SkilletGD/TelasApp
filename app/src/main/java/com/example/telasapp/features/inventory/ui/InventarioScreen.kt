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
import com.example.telasapp.features.inventory.ui.components.RolloItemPlaceholder
import com.example.telasapp.features.inventory.ui.components.SearchBar
import com.example.telasapp.features.inventory.ui.components.SearchSuggestions
import com.example.telasapp.features.inventory.viewmodel.InventarioViewModel
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    navController: NavController,
    vm: InventarioViewModel,
    snackbarHostState: SnackbarHostState
) {
    val rollos by vm.rollos.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()

    // Estado del gesto de arrastrar para refrescar
    val pullToRefreshState = rememberPullToRefreshState()

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

    // --- LOGICA DE DATOS ACTUALIZADA ---
    val filteredRollos = remember(rollos, searchQuery, selectedEstado, minMetros, maxMetros) {
        rollos.filter { r ->
            // 1. Buscamos por tipo, color o código
            val matchesSearch = searchQuery.isBlank() || listOf(r.tipo_tela, r.color ?: "", r.codigo).any {
                it.contains(searchQuery, ignoreCase = true)
            }

            // 2. Filtro de estado
            val matchesEstado = selectedEstado == null || r.estado == selectedEstado

            // 3. Filtro de metros (Usando el nuevo campo metros_reales_restantes)
            val actual = r.metros_reales_restantes ?: 0.0
            val min = minMetros.toDoubleOrNull() ?: 0.0
            val max = maxMetros.toDoubleOrNull() ?: Double.MAX_VALUE
            val inRange = actual >= min && actual <= max

            matchesSearch && matchesEstado && inRange
        }
    }

    val suggestions = remember(rollos, searchQuery) {
        if (searchQuery.length >= 2) {
            rollos.flatMap {
                // Usamos listOfNotNull para omitir automáticamente los campos que sean null
                listOfNotNull(it.tipo_tela, it.color, it.codigo)
            }
                .distinct()
                .filter { it.contains(searchQuery, ignoreCase = true) }
                .take(5)
        } else {
            emptyList()
        }
    }

    // --- DISEÑO ---
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            showFilters = showFilters,
            onFilterToggle = { showFilters = !showFilters }
        )

        // Envolvemos el contenido principal en el PullToRefreshBox
        PullToRefreshBox(
            isRefreshing = isLoading, // Se activa la animación mientras el VM carga
            onRefresh = { vm.cargarRollos() }, // Acción al soltar el arrastre
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (errorMessage != null) {
                    ErrorCard(message = errorMessage!!, onRetry = { vm.cargarRollos() })
                } else if (isLoading && rollos.isEmpty()) {
                    // Solo mostramos placeholders si es la PRIMERA carga (lista vacía)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
                    ) {
                        items(6) { RolloItemPlaceholder() }
                    }
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
}