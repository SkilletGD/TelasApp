package com.example.telasapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
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

    // 1. Nueva validación para el Splash
    val isSplashScreen = currentRoute == "splash"

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Modificamos la condición: NO mostrar si es Scanner O si es Splash
            if (currentRoute != Screen.Scanner.route && !isSplashScreen) {
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
            // Si es splash, la condición `in rootScreens` ya lo oculta automáticamente,
            // pero lo dejamos claro por seguridad
            if (currentRoute in rootScreens && !isSplashScreen) {
                BottomNavigationBar(navController)
            }
        },
        floatingActionButton = {
            if (currentRoute == Screen.Inventario.route && !isSplashScreen) {
                FloatingActionButton(onClick = { navController.navigate(Screen.Registro.route) }) {
                    Icon(Icons.Default.Add, "Agregar rollo")
                }
            }
        }
    ) { innerPadding ->
        // Importante: AppNavigation ahora recibe innerPadding para no quedar debajo de las barras
        // 2. Aquí está el truco: si es splash, pasamos 0 dp de padding
        AppNavigation(
            navController = navController,
            snackbarHostState = snackbarHostState,
            paddingValues = if (isSplashScreen) PaddingValues(0.dp) else innerPadding
        )
    }
}