package com.example.telasapp.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.telasapp.core.components.BottomNavigationBar
import com.example.telasapp.features.auth.viewmodel.AuthViewModel
import com.example.telasapp.features.auth.viewmodel.AuthState
import com.example.telasapp.data.models.UserRole
import com.example.telasapp.data.preferences.TokenManager
import com.example.telasapp.navigation.AppNavigation
import com.example.telasapp.navigation.Screen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelasMainScreen() {
    // MAGIA DE KOIN: El ViewModel se inyecta con todo su repositorio y cliente
    val authVm: AuthViewModel = koinViewModel()
    val tokenManager: TokenManager = koinInject()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }

    // 1. Observamos el rol desde el DataStore (Persistente)
    val savedRole by tokenManager.userRole.collectAsState(initial = null)

    // 2. Convertimos el String del DataStore a tu Enum UserRole
    val isAdmin = savedRole == UserRole.ADMIN.name

    // Definimos pantallas que NO tienen barras (Splash y Login)
    val noBarsScreens = listOf(Screen.Splash.route, Screen.Login.route)
    val isAuthFlow = currentRoute in noBarsScreens


    val rootScreens = listOf(
        Screen.Inventario.route,
        Screen.Carrito.route,
        Screen.Perfil.route
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // No mostrar si es Splash, Login o Scanner
            if (!isAuthFlow && currentRoute != Screen.Scanner.route) {
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
                            if (isAdmin) {
                                IconButton(onClick = { navController.navigate("reporteVentas") }) {
                                    Text("📊", style = MaterialTheme.typography.bodyLarge)
                                }
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
        bottomBar = {
            if (!isAuthFlow && currentRoute in rootScreens) {
                BottomNavigationBar(navController)
            }
        },
        floatingActionButton = {
            if (currentRoute == Screen.Inventario.route && isAdmin) {
                FloatingActionButton(onClick = { navController.navigate(Screen.Registro.route) }) {
                    Icon(Icons.Default.Add, "Agregar rollo")
                }
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            snackbarHostState = snackbarHostState,
            // Si es Splash o Login, usamos 0 padding para que sea pantalla completa
            paddingValues = if (isAuthFlow) PaddingValues(0.dp) else innerPadding,
            authVm = authVm,
            tokenManager = tokenManager
        )
    }
}