package com.example.telasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.telasapp.ui.TelasMainScreen
import com.example.telasapp.ui.theme.TelasAppTheme

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