package com.example.telasapp.features.export

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.example.telasapp.R
import com.example.telasapp.features.export.PrintAdapter // Adaptador personalizado
import java.io.File

class ExportService(private val context: Context) {

    fun imprimirPDF(file: File) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "${context.getString(R.string.app_name)} Document"

        // Usamos la clase que acabamos de crear
        val printAdapter = PrintAdapter(file)

        printManager.print(jobName, printAdapter, null)
    }

    fun compartirArchivo(file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir Etiqueta"))
    }
}