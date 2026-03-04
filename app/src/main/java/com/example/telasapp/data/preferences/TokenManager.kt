package com.example.telasapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val EMAIL_KEY = stringPreferencesKey("user_email") // Nueva llave
        private val ROLE_KEY = stringPreferencesKey("user_role")   // Nueva llave
    }

    // Actualizamos para guardar todo junto
    suspend fun saveUserData(token: String, email: String, role: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[EMAIL_KEY] = email
            preferences[ROLE_KEY] = role
        }
    }

    // Mantén saveToken por compatibilidad si lo usas en otros lados
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }

    // Nuevos flujos para observar el Email y el Rol desde cualquier pantalla
    val userEmail: Flow<String?> = context.dataStore.data.map { it[EMAIL_KEY] }
    val userRole: Flow<String?> = context.dataStore.data.map { it[ROLE_KEY] }

    // Limpia todo al cerrar sesión
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(EMAIL_KEY)
            preferences.remove(ROLE_KEY)
        }
    }
}