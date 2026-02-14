package com.example.telasapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.example.telasapp.features.sales.ui.ReporteVentasScreen
import com.example.telasapp.features.sales.viewmodel.SalesViewModel
import com.example.telasapp.features.scanner.ui.ScannerScreen

// Importa tus futuras pantallas (puedes crearlas vacías por ahora para que no de error)
// import com.example.telasapp.features.cart.ui.CartScreen
// import com.example.telasapp.features.profile.ui.ProfileScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    paddingValues: PaddingValues // <-- NUEVO: Recibe el padding del Scaffold
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Inventario.route,
        modifier = Modifier.padding(paddingValues) // <-- APLICA EL PADDING AQUÍ
    ) {
        // --- PANTALLAS PRINCIPALES (Bottom Bar) ---

        composable(Screen.Inventario.route) {
            val invViewModel: InventarioViewModel = viewModel()
            InventarioScreen(navController, invViewModel, snackbarHostState)
        }

        composable(Screen.Carrito.route) {
            // CartScreen(navController)
            Text("Pantalla de Carrito") // Temporal hasta que la crees
        }

        composable(Screen.Perfil.route) {
            // ProfileScreen(navController)
            Text("Pantalla de Perfil") // Temporal hasta que la crees
        }

        // --- PANTALLAS SECUNDARIAS ---

        composable(Screen.Registro.route) {
            val regVm: RegistrationViewModel = viewModel()
            val invVm: InventarioViewModel = viewModel()
            NuevoRolloScreen(navController, regVm, invVm, snackbarHostState)
        }

        composable(Screen.Scanner.route) {
            ScannerScreen(navController)
        }

        composable(Screen.Venta.route) { backStackEntry ->
            val salesVm: SalesViewModel = viewModel()
            val invVm: InventarioViewModel = viewModel()
            val rolloId = backStackEntry.arguments?.getString("rolloId")?.toIntOrNull()
            VentaScreen(navController, rolloId, salesVm, invVm, snackbarHostState)
        }

        composable(Screen.DetalleRollo.route) { backStackEntry ->
            val invVm: InventarioViewModel = viewModel()
            val detailVm: DetailViewModel = viewModel()
            val rolloId = backStackEntry.arguments?.getString("rolloId")?.toIntOrNull()
            DetailScreen(navController, rolloId, detailVm, invVm, snackbarHostState)
        }

        composable("reporteVentas") { // Puedes dejarla así o subirla a Screen.kt
            val salesVm: SalesViewModel = viewModel()
            ReporteVentasScreen(vm = salesVm)
        }
    }
}