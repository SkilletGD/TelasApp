package com.example.telasapp.features.cart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CartBottomSummary(
    itemCount: Int,
    totalMetros: Double,      // NUEVO: Para saber cuánta tela se va en total
    isLoading: Boolean,       // NUEVO: Para deshabilitar el botón mientras se guarda
    onCheckout: () -> Unit
) {
    Surface(
        // Elevación para que resalte sobre la lista de rollos
        tonalElevation = 12.dp,
        shadowElevation = 16.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding() // Respeta la barra de gestos de Android
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Fila de Cantidad de Ítems
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Rollos seleccionados:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "$itemCount",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // NUEVA Fila: Total de Metros (Dato clave para bodega)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total tela a cortar:", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${"%.2f".format(totalMetros)}m",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de acción con estado de carga
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isLoading && itemCount > 0, // Evita clics dobles y carritos vacíos
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) {
                    // Si está guardando en la DB, mostramos un circulito pequeño
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "FINALIZAR PEDIDO",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}