package com.example.telasapp.features.export

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import com.example.telasapp.data.models.Rollo
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream

// ... (mismos imports)

class LabelGenerator(private val context: Context) {

    private val width = 283
    private val height = 425

    fun generarPDFEtiqueta(rollo: Rollo): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        dibujarContenidoIndustrial(canvas, rollo)

        pdfDocument.finishPage(page)
        // Usamos el código del lote para el nombre del archivo
        val file = File(context.cacheDir, "Lote_${rollo.codigo}.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    private fun dibujarContenidoIndustrial(canvas: Canvas, rollo: Rollo) {
        val paint = Paint()
        canvas.drawColor(Color.WHITE)
        paint.color = Color.BLACK
        paint.isAntiAlias = true

        // 1. Encabezado
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText("SISTEMA DE INVENTARIO - TEXTILES", 20f, 30f, paint)

        // 2. ITEM / NOMBRE DE LA TELA
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        val nombreTela = "${rollo.tipo_tela}".uppercase()
        canvas.drawText(nombreTela, 20f, 70f, paint)

        paint.textSize = 18f
        canvas.drawText("COLOR: ${rollo.color ?: "N/A"}", 20f, 95f, paint)

        canvas.drawLine(20f, 110f, (width - 20).toFloat(), 110f, paint)

        // 3. DATOS DEL LOTE (Nuevos campos)
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.textSize = 16f
        canvas.drawText("LOTE: ${rollo.codigo}", 20f, 140f, paint)

        // Usamos metros_reales_restantes en lugar de cantidad_total
        val stockActual = rollo.metros_reales_restantes ?: 0.0
        canvas.drawText("STOCK TOTAL: $stockActual m", 20f, 170f, paint)

        // Cantidad de rollos físicos en el lote
        val cantRollos = rollo.rollos_disponibles ?: 0
        canvas.drawText("ROLLOS ACTIVOS: $cantRollos", 20f, 195f, paint)

        // 4. IDENTIFICADOR GRANDE (ID del Lote)
        paint.textSize = 14f
        canvas.drawText("ID SISTEMA:", 20f, 250f, paint)
        paint.textSize = 60f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("${rollo.id}", 20f, 310f, paint)

        // 5. CÓDIGO QR (Actualizado con datos reales)
        val qrSize = 110
        val qrBitmap = generarBitmapQR(rollo, qrSize)
        qrBitmap?.let {
            canvas.drawBitmap(it, (width - qrSize - 20).toFloat(), (height - qrSize - 20).toFloat(), null)
        }

        // Pie de página pequeño
        paint.textSize = 8f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
        canvas.drawText("Generado el: ${rollo.fecha_compra ?: ""}", 20f, 410f, paint)
    }

    private fun generarBitmapQR(rollo: Rollo, size: Int): Bitmap? {
        // El QR ahora incluye información del Lote y stock actual
        val content = """
            LOTE ID: ${rollo.id}
            TELA: ${rollo.tipo_tela}
            METROS: ${rollo.metros_reales_restantes ?: 0.0}
            ROLLOS: ${rollo.rollos_disponibles ?: 0}
            PRECIO: $${rollo.precio}
        """.trimIndent()

        return try {
            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) { null }
    }
}