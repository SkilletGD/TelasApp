package com.example.telasapp.di


import com.example.telasapp.data.preferences.TokenManager
import com.example.telasapp.features.auth.viewmodel.AuthConfirmViewModel
import com.example.telasapp.features.auth.data.repository.AuthRepository
import com.example.telasapp.features.auth.usecase.LoginUseCase
import com.example.telasapp.features.auth.viewmodel.AuthViewModel
import com.example.telasapp.features.cart.viewmodel.CartViewModel
import com.example.telasapp.features.detail.viewmodel.DetailViewModel
import com.example.telasapp.features.inventory.viewmodel.InventarioViewModel
import com.example.telasapp.features.registration.viewmodel.RegistrationViewModel
import com.example.telasapp.features.sales.viewmodel.SalesViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // 1. Proveer HttpClient (Singleton)
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true // Convierte nulls a los valores por defecto que acabamos de poner
                })

            }
        }
    }

    // 2. Proveer TokenManager (usa androidContext() automáticamente)
    single { TokenManager(androidContext()) }

    // 3. Proveer Repositorio (get() busca automáticamente el Client y el TokenManager)
    single { AuthRepository(get(), get()) }

    single { LoginUseCase(get()) }


    viewModel { SalesViewModel() }
    viewModel { InventarioViewModel() }
    viewModel { AuthConfirmViewModel(get()) }
    // AGREGAMOS EL QUE FALTABA:
    viewModel { RegistrationViewModel() } // <--- ESTO SOLUCIONA EL CRASH
    viewModel{ DetailViewModel() }

    // 4. Proveer ViewModel
    viewModel { AuthViewModel(get()) }
}