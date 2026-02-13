package com.example.telasapp.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.telasapp.features.inventory.ui.InventarioScreen
import com.example.telasapp.ui.screens.NuevoRolloScreen
import com.example.telasapp.ui.screens.VentaScreen
import com.example.telasapp.ui.viewmodel.InventarioViewModel
import com.example.telasapp.ui.screens.DetalleRolloScreen
import com.example.telasapp.ui.screens.ReporteVentasScreen  // ¡NUEVA IMPORTACIÓN!
import com.example.telasapp.ui.screens.ScannerScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: InventarioViewModel,
    snackbarHostState: SnackbarHostState
) {
    NavHost(navController = navController, startDestination = "inventario") {
        composable("inventario") {
            InventarioScreen(navController = navController, vm = viewModel, snackbarHostState = snackbarHostState)
        }
        composable("nuevoRollo") {
            NuevoRolloScreen(navController = navController, vm = viewModel, snackbarHostState = snackbarHostState)
        }
        composable("venta/{rolloId}") { backStackEntry ->
            val rolloId = backStackEntry.arguments?.getString("rolloId")?.toIntOrNull()
            VentaScreen(
                navController = navController,
                rolloId = rolloId,
                vm = viewModel,
                snackbarHostState = snackbarHostState
            )
        }
        // --- ESTO ES LO QUE TE FALTA ---
        composable("scanner") {
            ScannerScreen(navController)
        }
        composable("detalleRollo/{rolloId}") { backStackEntry ->
            val rolloId = backStackEntry.arguments?.getString("rolloId")?.toIntOrNull()
            DetalleRolloScreen(
                navController = navController,
                rolloId = rolloId,
                vm = viewModel,
                snackbarHostState = snackbarHostState
            )
        }
        // ¡NUEVA RUTA PARA REPORTES DE VENTAS!
        composable("reporteVentas") {
            ReporteVentasScreen(vm = viewModel)
        }
    }
}