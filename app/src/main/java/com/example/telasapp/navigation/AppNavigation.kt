package com.example.telasapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.telasapp.data.preferences.TokenManager
import com.example.telasapp.features.auth.viewmodel.AuthConfirmViewModel
import com.example.telasapp.features.auth.ui.LoginScreen
import com.example.telasapp.features.auth.viewmodel.AuthViewModel
import com.example.telasapp.features.cart.ui.CartScreen
import com.example.telasapp.features.cart.viewmodel.CartViewModel
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
import com.example.telasapp.features.splash.SplashScreen
import org.koin.androidx.compose.koinViewModel

// Importa tus futuras pantallas (puedes crearlas vacías por ahora para que no de error)
// import com.example.telasapp.features.cart.ui.CartScreen
// import com.example.telasapp.features.profile.ui.ProfileScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    paddingValues: PaddingValues,
    authVm: AuthViewModel,
    tokenManager: TokenManager
) {
    // Instancia compartida del carrito para que persista entre pantallas de venta
    val sharedCartVm: CartViewModel = viewModel()

    // Observamos el estado de autenticación del ViewModel
    val authState by authVm.authState.collectAsState()

    // Agrega esto arriba, junto a las otras observaciones (como la de authState)
    val userEmail by tokenManager.userEmail.collectAsState(initial = "")
// Instanciamos el ViewModel de confirmación (puedes usar koin o viewModel())
    val authConfirmVm: AuthConfirmViewModel = koinViewModel()

    NavHost(
        navController = navController,
        // El Splash decide a dónde ir, pero podrías cambiarlo dinámicamente aquí si quisieras
        startDestination = Screen.Splash.route,
        modifier = Modifier.padding(paddingValues)
    ) {

        // --- 1. SPLASH ---
        composable(Screen.Splash.route) {
            SplashScreen(
                navController = navController,
                tokenManager = tokenManager // <-- Pásale el objeto que inyectamos con Koin
            )
        }

        // --- 2. AUTH ---
        composable(Screen.Login.route) {
            LoginScreen(vm = authVm, navController = navController)
        }

        // --- 3. PANTALLAS PRINCIPALES ---
        composable(Screen.Inventario.route) {
            val invViewModel: InventarioViewModel = viewModel()
            InventarioScreen(navController, invViewModel, snackbarHostState)
        }

        // --- 4. PERFIL ---
        composable(Screen.Perfil.route) {
            com.example.telasapp.features.user.ui.ProfileScreen(
                navController = navController,
                authVm = authVm,
                tokenManager = tokenManager
            )
        }

        // --- 5. REGISTRO DE NUEVOS ROLLOS ---
        composable(Screen.Registro.route) {
            val regVm: RegistrationViewModel = koinViewModel()
            val invVm: InventarioViewModel = koinViewModel()

            NuevoRolloScreen(
                navController = navController,
                regVm = regVm,
                invVm = invVm,
                authConfirmVm = authConfirmVm, // <-- NUEVO
                userEmail = userEmail ?: "",   // <-- NUEVO
                snackbarHostState = snackbarHostState
            )
        }

        // --- 6. ESCÁNER ---
        composable(Screen.Scanner.route) {
            ScannerScreen(navController)
        }

        // --- 7. VENTA ---
        composable(
            route = Screen.Venta.route,
            arguments = listOf(navArgument("rolloId") { type = NavType.IntType }) // <-- Cambiar a IntType
        ) { backStackEntry ->
            val rolloId = backStackEntry.arguments?.getInt("rolloId") // <-- Usar getInt

            val salesVm: SalesViewModel = koinViewModel()
            val invVm: InventarioViewModel = koinViewModel()

            VentaScreen(
                navController = navController,
                rolloId = rolloId,
                salesVm = salesVm,
                cartVm = sharedCartVm,
                invVm = invVm,
                authConfirmVm = authConfirmVm,
                userEmail = userEmail ?: "",
                snackbarHostState = snackbarHostState
            )
        }

        // --- 8. CARRITO ---
        composable(Screen.Carrito.route) {
            val salesVm: SalesViewModel = viewModel()
            val invVm: InventarioViewModel = viewModel()
            CartScreen(
                navController = navController,
                cartVm = sharedCartVm,
                salesVm = salesVm,
                invVm = invVm
            )
        }

        // --- 9. DETALLE DEL ROLLO (SOLUCIÓN AL CARGANDO INFINITO) ---
        composable(
            route = Screen.DetalleRollo.route, // "detalleRollo/{rolloId}"
            arguments = listOf(
                navArgument("rolloId") { type = NavType.IntType } // 1. Definimos que es un entero
            )
        ) { backStackEntry ->
            val invVm: InventarioViewModel = koinViewModel()
            val detailVm: DetailViewModel = koinViewModel()
            val authConfirmVm: AuthConfirmViewModel = koinViewModel()

            // 2. Extraemos como Int directamente (sin toIntOrNull)
            val rolloId = backStackEntry.arguments?.getInt("rolloId")

            DetailScreen(
                navController = navController,
                rolloId = rolloId,
                detailVm = detailVm,
                invVm = invVm,
                authVm = authVm,
                authConfirmVm = authConfirmVm,
                tokenManager = tokenManager,
                snackbarHostState = snackbarHostState
            )
        }

        // --- 10. REPORTES ---
        composable("reporteVentas") {
            val salesVm: SalesViewModel = viewModel()
            ReporteVentasScreen(vm = salesVm)
        }
    }
}