package com.example.telasapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.telasapp.core.components.BottomNavigationBar // IMPORTA TU BARRA
import com.example.telasapp.navigation.AppNavigation
import com.example.telasapp.navigation.Screen // IMPORTA TUS RUTAS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelasMainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }

    // Definimos qué pantallas son las "principales" (donde se verá la BottomBar)
    val rootScreens = listOf(
        Screen.Inventario.route,
        Screen.Carrito.route,
        Screen.Perfil.route
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // No mostramos barra en el scanner
            if (currentRoute != Screen.Scanner.route) {
                TopAppBar(
                    title = {
                        Text(when {
                            currentRoute == Screen.Inventario.route -> "Inventario"
                            currentRoute == Screen.Carrito.route -> "Mi Carrito"
                            currentRoute == Screen.Perfil.route -> "Mi Perfil"
                            currentRoute == Screen.Registro.route -> "Nuevo Rollo"
                            currentRoute?.startsWith("venta") == true -> "Registrar Venta"
                            currentRoute?.startsWith("detalle") == true -> "Detalle del Rollo"
                            else -> "TelasApp"
                        })
                    },
                    navigationIcon = {
                        // Solo mostramos flecha si NO es una pantalla raíz
                        if (currentRoute !in rootScreens) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, "Atrás")
                            }
                        }
                    },
                    actions = {
                        if (currentRoute == Screen.Inventario.route) {
                            IconButton(onClick = { navController.navigate(Screen.Scanner.route) }) {
                                Icon(Icons.Default.QrCodeScanner, "Escanear QR")
                            }
                            IconButton(onClick = { navController.navigate("reporteVentas") }) {
                                Text("📊", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        // --- AQUÍ APLICAMOS LA BOTTOM BAR ---
        bottomBar = {
            // Solo se muestra en las pantallas principales (Home, Carrito, Perfil)
            if (currentRoute in rootScreens) {
                BottomNavigationBar(navController)
            }
        },
        floatingActionButton = {
            // El FAB solo en el inventario para no estorbar en el perfil o carrito
            if (currentRoute == Screen.Inventario.route) {
                FloatingActionButton(onClick = { navController.navigate(Screen.Registro.route) }) {
                    Icon(Icons.Default.Add, "Agregar rollo")
                }
            }
        }
    ) { innerPadding ->
        // Importante: AppNavigation ahora recibe innerPadding para no quedar debajo de las barras
        AppNavigation(
            navController = navController,
            snackbarHostState = snackbarHostState,
            paddingValues = innerPadding
        )
    }
}