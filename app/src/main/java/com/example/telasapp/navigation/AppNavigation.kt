package com.example.telasapp.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.telasapp.features.detail.ui.DetailScreen
import com.example.telasapp.features.detail.viewmodel.DetailViewModel
import com.example.telasapp.features.inventory.ui.InventarioScreen
import com.example.telasapp.features.inventory.viewmodel.InventarioViewModel
import com.example.telasapp.features.registration.ui.NuevoRolloScreen
import com.example.telasapp.features.registration.viewmodel.RegistrationViewModel
import com.example.telasapp.features.sales.ui.VentaScreen
import com.example.telasapp.features.sales.ui.ReporteVentasScreen  // ¡NUEVA IMPORTACIÓN!
import com.example.telasapp.features.sales.viewmodel.SalesViewModel
import com.example.telasapp.features.scanner.ui.ScannerScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    NavHost(navController = navController, startDestination = "inventario") {
        composable("inventario") {
            val invViewModel: InventarioViewModel = viewModel()
            InventarioScreen(
                navController = navController,
                vm = invViewModel,
                snackbarHostState = snackbarHostState
            )
        }
        composable("nuevoRollo") {
            val regVm: RegistrationViewModel = viewModel()
            val invVm: InventarioViewModel = viewModel()

            NuevoRolloScreen(
                navController = navController,
                regVm = regVm,
                invVm = invVm,
                snackbarHostState = snackbarHostState
            )
        }
        composable("venta/{rolloId}") { backStackEntry ->
            val salesVm: SalesViewModel = viewModel()
            val invVm: InventarioViewModel = viewModel()
            val rolloId = backStackEntry.arguments?.getString("rolloId")?.toIntOrNull()

            VentaScreen(
                navController = navController,
                rolloId = rolloId,
                salesVm = salesVm,
                invVm = invVm,
                snackbarHostState = snackbarHostState
            )
        }
        // --- ESTO ES LO QUE TE FALTA ---
        composable("scanner") {
            ScannerScreen(navController)
        }
        composable("detalleRollo/{rolloId}") { backStackEntry ->
            // Importamos los ViewModels de sus respectivas features
            val invVm: InventarioViewModel = viewModel()
            val detailVm: DetailViewModel = viewModel()
            val rolloId = backStackEntry.arguments?.getString("rolloId")?.toIntOrNull()

            DetailScreen(
                navController = navController,
                rolloId = rolloId,
                detailVm = detailVm,
                invVm = invVm,
                snackbarHostState = snackbarHostState
            )
        }
        // ¡NUEVA RUTA PARA REPORTES DE VENTAS!
        composable("reporteVentas") {
            val salesVm: SalesViewModel = viewModel()
            ReporteVentasScreen(vm = salesVm)
        }
    }
}