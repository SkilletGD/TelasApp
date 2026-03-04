package com.example.telasapp

import android.app.Application
import com.example.telasapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class TelasApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Loguea errores de Koin en el Logcat (útil para debug)
            androidLogger(Level.ERROR)
            // Pasa el contexto de la app para que TokenManager funcione
            androidContext(this@TelasApp)
            // Carga tus módulos
            modules(appModule)
        }
    }
}