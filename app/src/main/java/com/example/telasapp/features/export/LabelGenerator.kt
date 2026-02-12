package com.example.telasapp.features.export

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import com.example.telasapp.data.models.Rollo
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream

class LabelGenerator(private val context: Context) {

    // 10cm x 15cm (Vertical) en puntos de impresión (72 dpi)
    private val width = 283
    private val height = 425

    fun generarPDFEtiqueta(rollo: Rollo): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        dibujarContenidoIndustrial(canvas, rollo)

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "Etiqueta_${rollo.codigo}.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    }

    private fun dibujarContenidoIndustrial(canvas: Canvas, rollo: Rollo) {
        val paint = Paint()

        // Fondo Blanco (Estándar de etiqueta)
        canvas.drawColor(Color.WHITE)

        // Configuración de Texto Negro
        paint.color = Color.BLACK
        paint.isAntiAlias = true

        // 1. Encabezado "PREMIUM" (Pequeño arriba)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText("PREMIUM QUALITY", 20f, 40f, paint)

        // 2. ITEM / NOMBRE DE LA TELA (Grande y Negrita)
        paint.textSize = 28f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        // Usamos el nombre y color de tu base de datos
        val nombreTela = "${rollo.tipo_tela} ${rollo.color}".uppercase()
        canvas.drawText(nombreTela, 20f, 100f, paint)

        // Línea divisora
        paint.strokeWidth = 2f
        canvas.drawLine(20f, 120f, (width - 20).toFloat(), 120f, paint)

        // 3. CODE y LENGTH (Datos dinámicos)
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.textSize = 18f
        canvas.drawText("CODE: ${rollo.codigo}", 20f, 160f, paint)
        canvas.drawText("LENGTH: ${rollo.cantidad_total} m", 20f, 190f, paint)

        // 4. ROLL NO y LOTE (Grande al centro/abajo)
        paint.textSize = 18f
        canvas.drawText("ROLL NO: ${rollo.id}", 20f, 260f, paint)

        paint.textSize = 55f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        // El ID del rollo como número de lote/identificador grande
        canvas.drawText("${rollo.id}", 20f, 320f, paint)

        // 5. CÓDIGO QR (Resumen de datos)
        val qrSize = 100 // aprox 3.5cm
        val qrBitmap = generarBitmapQR(rollo, qrSize)
        qrBitmap?.let {
            // Posicionado en la esquina inferior derecha
            canvas.drawBitmap(it, (width - qrSize - 20).toFloat(), (height - qrSize - 20).toFloat(), null)
        }
    }

    private fun generarBitmapQR(rollo: Rollo, size: Int): Bitmap? {
        // El QR contiene el resumen que pediste
        val content = """
            ID: ${rollo.id}
            TEL: ${rollo.tipo_tela}
            COL: ${rollo.color}
            COD: ${rollo.codigo}
            MET: ${rollo.cantidad_total}
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