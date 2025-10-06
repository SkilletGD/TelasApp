package com.example.telasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.telasapp.ui.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(navController: NavController, vm: InventarioViewModel = viewModel()) {
    val rollos by vm.rollos.collectAsState()

    LaunchedEffect(Unit) {
        vm.cargarRollos()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Inventario de Rollos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Navegar a nueva pantalla */ }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            rollos.forEach { rollo ->
                Text("${rollo.tipo_tela} - ${rollo.color} (${rollo.cantidad_restante}m restantes)")
                Divider()
            }
        }
    }
}
