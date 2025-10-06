package com.example.telasapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.telasapp.ui.screens.InventarioScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "inventario") {
        composable("inventario") { InventarioScreen(navController) }
        // composable("nuevo_rollo") { NuevoRolloScreen(navController) } // Próximo paso
    }
}
