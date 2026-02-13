package com.example.telasapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.telasapp.navigation.AppNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelasMainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // El SnackbarHostState ahora es UNICO para toda la app
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // No mostramos barra en el scanner para que sea pantalla completa
            if (currentRoute != "scanner") {
                TopAppBar(
                    title = {
                        Text(when {
                            currentRoute == "inventario" -> "Inventario de Telas"
                            currentRoute == "nuevoRollo" -> "Nuevo Rollo"
                            currentRoute == "reporteVentas" -> "Reporte de Ventas"
                            currentRoute?.startsWith("venta") == true -> "Registrar Venta"
                            currentRoute?.startsWith("detalleRollo") == true -> "Detalle del Rollo"
                            else -> "TelasApp"
                        })
                    },
                    navigationIcon = {
                        if (currentRoute != "inventario") {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, "Atrás")
                            }
                        }
                    },
                    actions = {
                        // Solo mostramos estas acciones si estamos en la pantalla principal
                        if (currentRoute == "inventario") {
                            // BOTÓN DEL SCANNER (El que te faltaba)
                            IconButton(onClick = { navController.navigate("scanner") }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Escanear QR"
                                )
                            }

                            // BOTÓN DE REPORTES
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
        floatingActionButton = {
            // El botón de AGREGAR (FAB) vuelve a aparecer aquí
            if (currentRoute == "inventario") {
                FloatingActionButton(onClick = { navController.navigate("nuevoRollo") }) {
                    Icon(Icons.Default.Add, "Agregar rollo")
                }
            }
        }
    ) { innerPadding ->
        // El padding del Scaffold se aplica aquí una sola vez
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavigation(
                navController = navController,
                snackbarHostState = snackbarHostState // Lo pasamos para que las screens lo usen
            )
        }
    }
}