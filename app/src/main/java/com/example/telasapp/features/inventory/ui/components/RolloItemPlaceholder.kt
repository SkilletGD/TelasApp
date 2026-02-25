package com.example.telasapp.features.inventory.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.telasapp.core.components.shimmerLoadingAnimation

// Asegúrate de importar tu modificador:
// import com.example.telasapp.ui.utils.shimmerLoadingAnimation

@Composable
fun RolloItemPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Simulación: Tipo de tela y Color
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(20.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerLoadingAnimation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Simulación: Código
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .height(12.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerLoadingAnimation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Simulación: Metros disponibles
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(18.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmerLoadingAnimation()
                    )
                }

                // Simulación: Badge de estado (el cuadrito de la derecha)
                Box(
                    modifier = Modifier
                        .size(width = 70.dp, height = 24.dp)
                        .clip(MaterialTheme.shapes.small)
                        .shimmerLoadingAnimation()
                )
            }

            // Simulación: El botón de Registrar Venta
            // Ponemos un espacio y un bloque que imita al botón para que la tarjeta mida lo mismo
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerLoadingAnimation()
            )
        }
    }
}