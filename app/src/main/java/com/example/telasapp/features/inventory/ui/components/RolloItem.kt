package com.example.telasapp.features.inventory.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.telasapp.data.models.Rollo
import coil.compose.AsyncImage
import coil.request.ImageRequest
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
fun RolloItem(
    rollo: Rollo,
    onVentaClick: (Int) -> Unit,
    onDetailClick: (Int) -> Unit
) {
    val metrosRestantes = rollo.metros_reales_restantes ?: 0.0
    val rollosActivos = rollo.rollos_disponibles ?: 0
    val estadoActual = rollo.estado ?: "Disponible"
    val imagenUrl = rollo.imagen_url

    LaunchedEffect(imagenUrl) {
        println("DEBUG_IMAGEN: La URL recibida es -> $imagenUrl")
    }

    // PRUEBA DIRECTA (Borra esto después de probar)
    val urlDePrueba = "https://cdn.pixabay.com/photo/2021/03/12/08/51/shorturl-6089108_1280.jpg"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailClick(rollo.id ?: 0) },
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp) // Esquinas más redondeadas para un look moderno
    ) {
        // --- CAMBIAMOS A COLUMN PARA PONER LA IMAGEN ARRIBA ---
        Column {
            // 🖼️ CONTENEDOR DE IMAGEN (Ahora arriba y ancho completo)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Altura fija para la imagen
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (!imagenUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imagenUrl, // Pasamos el String directo
                        contentDescription = "Imagen de tela",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                        error = painterResource(id = android.R.drawable.ic_menu_report_image)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "Sin imagen",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                }

                // 🏷️ BADGE DE ESTADO (Flotando sobre la imagen arriba a la derecha)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    color = when (estadoActual) {
                        "Disponible" -> Color(0xFFE8F5E8).copy(alpha = 0.9f)
                        "Agotado" -> Color(0xFFFFEBEE).copy(alpha = 0.9f)
                        else -> Color(0xFFFFF3E0).copy(alpha = 0.9f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = estadoActual,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (estadoActual) {
                            "Disponible" -> Color(0xFF2E7D32)
                            "Agotado" -> Color(0xFFC62828)
                            else -> Color(0xFFEF6C00)
                        }
                    )
                }
            }

            // 📝 INFORMACIÓN DEL ROLLO (Debajo de la imagen)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${rollo.tipo_tela}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Color: ${rollo.color ?: "N/A"} | Lote: ${rollo.codigo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Metros disponibles
                    Column {
                        Text(
                            text = "${formatNumber(metrosRestantes)}m",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Disponibles",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }

                    // Rollos físicos
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$rollosActivos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (rollosActivos <= 1) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (rollosActivos == 1) "Rollo activo" else "Rollos activos",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// Función auxiliar para formatear números

fun formatNumber(value: Double): String {
    return if (value == value.toInt().toDouble()) {
        value.toInt().toString()
    } else {
        String.format("%.2f", value)
    }
}