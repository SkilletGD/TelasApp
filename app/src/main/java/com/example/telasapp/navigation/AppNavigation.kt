package com.example.telasapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.telasapp.ui.screens.InventarioScreen
import com.example.telasapp.ui.screens.NuevoRolloScreen
import com.example.telasapp.ui.screens.VentaScreen
import com.example.telasapp.ui.viewmodel.InventarioViewModel
import com.example.telasapp.ui.screens.DetalleRolloScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: InventarioViewModel  // Recibir el ViewModel desde MainActivity
) {
    NavHost(navController = navController, startDestination = "inventario") {
        composable("inventario") {
            InventarioScreen(navController = navController, vm = viewModel)
        }
        composable("nuevoRollo") {
            NuevoRolloScreen(navController = navController, vm = viewModel)
        }
        composable("venta/{rolloId}") { backStackEntry ->
            val rolloId = backStackEntry.arguments?.getString("rolloId")?.toIntOrNull()
            VentaScreen(
                navController = navController,
                rolloId = rolloId,
                vm = viewModel  // Pasar la misma instancia
            )
        }
        composable("detalleRollo/{rolloId}") { backStackEntry ->
            val rolloId = backStackEntry.arguments?.getString("rolloId")?.toIntOrNull()
            DetalleRolloScreen(
                navController = navController,
                rolloId = rolloId,
                vm = viewModel  // Pasar la misma instancia
            )
        }
    }
}