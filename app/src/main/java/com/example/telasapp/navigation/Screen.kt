package com.example.telasapp.navigation

// Archivo: com.example.telasapp.navigation.Screen.kt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    // Pantallas principales (Bottom Bar)
    object Inventario : Screen("inventario", "Inicio", Icons.Default.Home)
    object Carrito : Screen("carrito", "Carrito", Icons.Default.ShoppingCart)
    object Perfil : Screen("perfil", "Perfil", Icons.Default.Person)

    // Pantallas secundarias
    object DetalleRollo : Screen("detalleRollo/{rolloId}")
    object Venta : Screen("venta/{rolloId}")
    object Registro : Screen("registro")
    // Asegúrate de que esta línea exista:
    object Scanner : Screen("scanner")

    object ReporteVentas : Screen("reporteVentas")
}