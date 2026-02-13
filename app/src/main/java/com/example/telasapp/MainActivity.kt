package com.example.telasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.telasapp.navigation.AppNavigation
import com.example.telasapp.ui.TelasMainScreen
import com.example.telasapp.ui.theme.TelasAppTheme
import com.example.telasapp.ui.viewmodel.InventarioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TelasAppTheme {
                TelasMainScreen()
            }
        }
    }
}