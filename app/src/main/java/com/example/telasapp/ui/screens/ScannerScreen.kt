package com.example.telasapp.ui.screens

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.telasapp.features.scanner.QRScannerAnalyzer
import java.util.concurrent.Executors

@Composable
fun ScannerScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // 1. LA CÁMARA (Ocupa todo el fondo)
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor, QRScannerAnalyzer { qrContent ->
                                val id = qrContent.lines()
                                    .firstOrNull { line -> line.startsWith("ID:") }
                                    ?.replace("ID:", "")?.trim()?.toIntOrNull()

                                if (id != null) {
                                    cameraProvider.unbindAll()
                                    ContextCompat.getMainExecutor(context).execute {
                                        navController.navigate("detalleRollo/$id") {
                                            popUpTo("scanner") { inclusive = true }
                                        }
                                    }
                                }
                            })
                        }
                    cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. INTERFAZ "CLEAN" (Solo bordes y sombras)
        Column(modifier = Modifier.fillMaxSize()) {

            // Botón de cerrar arriba a la izquierda
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(top = 40.dp, start = 20.dp)
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(50))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
            }

            Spacer(modifier = Modifier.weight(1f))

            // EL RECUADRO DE ENFOQUE (Simple y estético)
            Box(
                modifier = Modifier
                    .size(260.dp) // Tamaño del cuadro
                    .align(Alignment.CenterHorizontally)
                    .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            )

            Spacer(modifier = Modifier.weight(1f))

            // TEXTO INFERIOR
            Text(
                text = "ESCANEAR CÓDIGO",
                color = Color.White,
                fontWeight = FontWeight.Light,
                letterSpacing = 4.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 60.dp)
            )
        }
    }
}