package com.example.telasapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.telasapp.ui.screens.InventarioScreen
import com.example.telasapp.ui.screens.NuevoRolloScreen
import com.example.telasapp.ui.screens.VentaScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "inventario") {
        composable("inventario") {
            InventarioScreen(navController = navController)
        }
        composable("nuevoRollo") {
            NuevoRolloScreen(navController = navController)
        }
        composable("venta/{rolloId}") { backStackEntry ->
            val rolloId = backStackEntry.arguments?.getString("rolloId")?.toIntOrNull()
            VentaScreen(
                navController = navController,
                rolloId = rolloId
            )
        }
    }
}