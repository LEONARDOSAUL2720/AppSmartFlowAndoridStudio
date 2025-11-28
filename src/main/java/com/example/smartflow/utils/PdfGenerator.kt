package com.example.smartflow.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.smartflow.R
import com.example.smartflow.data.models.Receta
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator {

    companion object {
        private const val PAGE_WIDTH = 595  // A4 width en puntos
        private const val PAGE_HEIGHT = 842 // A4 height en puntos
        private const val MARGIN = 50f
        private const val LINE_HEIGHT = 20f

        fun generarRecetaPDF(context: Context, receta: Receta): File? {
            try {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                // ===== PAINTS =====
                val paintTitulo = Paint().apply {
                    color = Color.parseColor("#2A6FB0")
                    textSize = 28f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }

                val paintSubtitulo = Paint().apply {
                    color = Color.parseColor("#2A6FB0")
                    textSize = 16f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }

                val paintTexto = Paint().apply {
                    color = Color.parseColor("#333333")
                    textSize = 12f
                }

                val paintTextoBold = Paint().apply {
                    color = Color.parseColor("#333333")
                    textSize = 12f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }

                val paintTextoSecundario = Paint().apply {
                    color = Color.parseColor("#666666")
                    textSize = 11f
                }

                val paintLinea = Paint().apply {
                    color = Color.parseColor("#E0E0E0")
                    strokeWidth = 2f
                }

                val paintLineaAzul = Paint().apply {
                    color = Color.parseColor("#2A6FB0")
                    strokeWidth = 3f
                }

                var yPosition = MARGIN

                // ===== HEADER CON LOGO =====
                try {
                    val logo = BitmapFactory.decodeResource(context.resources, R.drawable.icon_smart_flow)
                    val logoScaled = Bitmap.createScaledBitmap(logo, 80, 80, true)
                    canvas.drawBitmap(logoScaled, MARGIN, yPosition, null)
                } catch (e: Exception) {
                    // Si falla el logo, continuar sin él
                }

                // Título al lado del logo
                yPosition += 35f
                val paintAppName = Paint().apply {
                    color = Color.parseColor("#2A6FB0")
                    textSize = 20f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText("SmartFlow Medical", MARGIN + 100f, yPosition, paintAppName)

                yPosition += 18f
                val paintSubtituloApp = Paint().apply {
                    color = Color.parseColor("#666666")
                    textSize = 11f
                }
                canvas.drawText("Sistema de Gestión Médica", MARGIN + 100f, yPosition, paintSubtituloApp)

                yPosition += 30f

                // Línea separadora azul
                canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, paintLineaAzul)
                yPosition += 25f

                // Título principal centrado
                canvas.drawText("RECETA MÉDICA", PAGE_WIDTH / 2f, yPosition, paintTitulo)
                yPosition += 35f

                // ===== INFORMACIÓN DEL MÉDICO =====
                // Fondo gris claro
                val paintFondo = Paint().apply {
                    color = Color.parseColor("#F4F7F9")
                    style = Paint.Style.FILL
                }
                canvas.drawRect(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition + 110f, paintFondo)

                yPosition += 20f

                canvas.drawText("MÉDICO TRATANTE", MARGIN + 10f, yPosition, paintSubtitulo)
                yPosition += 25f

                canvas.drawText("Dr(a). ${receta.medico.nombre}", MARGIN + 10f, yPosition, paintTextoBold)
                yPosition += LINE_HEIGHT

                canvas.drawText("Especialidad: ${receta.medico.especialidad}", MARGIN + 10f, yPosition, paintTexto)
                yPosition += LINE_HEIGHT

                if (!receta.medico.cedulaProfesional.isNullOrEmpty()) {
                    canvas.drawText("Cédula Profesional: ${receta.medico.cedulaProfesional}", MARGIN + 10f, yPosition, paintTextoSecundario)
                    yPosition += LINE_HEIGHT
                }

                canvas.drawText("Email: ${receta.medico.email}", MARGIN + 10f, yPosition, paintTextoSecundario)
                yPosition += 30f

                // ===== FECHA Y HORA =====
                val fechaFormateada = formatearFecha(receta.fecha)
                canvas.drawText("Fecha de emisión: $fechaFormateada", MARGIN, yPosition, paintTexto)
                yPosition += LINE_HEIGHT

                canvas.drawText("Hora: ${receta.cita.hora}", MARGIN, yPosition, paintTexto)
                yPosition += 30f

                // Línea separadora
                canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, paintLinea)
                yPosition += 25f

                // ===== MEDICAMENTOS PRESCRITOS =====
                canvas.drawText("MEDICAMENTOS PRESCRITOS", MARGIN, yPosition, paintSubtitulo)
                yPosition += 28f

                receta.medicamentos.forEachIndexed { index, medicamento ->
                    // Fondo alternado para cada medicamento
                    if (index % 2 == 0) {
                        val paintFondoMed = Paint().apply {
                            color = Color.parseColor("#FAFBFC")
                            style = Paint.Style.FILL
                        }
                        canvas.drawRect(MARGIN, yPosition - 5f, PAGE_WIDTH - MARGIN, yPosition + 75f, paintFondoMed)
                    }

                    // Número y nombre del medicamento
                    val paintNumero = Paint().apply {
                        color = Color.parseColor("#2A6FB0")
                        textSize = 14f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    }
                    canvas.drawText("${index + 1}.", MARGIN + 5f, yPosition, paintNumero)
                    canvas.drawText(medicamento.nombre, MARGIN + 25f, yPosition, paintTextoBold)
                    yPosition += LINE_HEIGHT + 3f

                    // Detalles con bullet points
                    canvas.drawText("• Dosis: ${medicamento.dosis}", MARGIN + 25f, yPosition, paintTexto)
                    yPosition += LINE_HEIGHT

                    canvas.drawText("• Frecuencia: ${medicamento.frecuencia}", MARGIN + 25f, yPosition, paintTexto)
                    yPosition += LINE_HEIGHT

                    canvas.drawText("• Duración: ${medicamento.duracion}", MARGIN + 25f, yPosition, paintTexto)
                    yPosition += LINE_HEIGHT + 15f
                }

                yPosition += 10f

                // ===== OBSERVACIONES =====
                if (!receta.observaciones.isNullOrEmpty()) {
                    canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, paintLinea)
                    yPosition += 25f

                    canvas.drawText("OBSERVACIONES E INDICACIONES", MARGIN, yPosition, paintSubtitulo)
                    yPosition += 23f

                    // Fondo
                    val paintFondoObs = Paint().apply {
                        color = Color.parseColor("#FFF9E6")
                        style = Paint.Style.FILL
                    }
                    val lineas = dividirTextoEnLineas(receta.observaciones, PAGE_WIDTH - (MARGIN * 2) - 20f, paintTextoSecundario)
                    val alturaObs = lineas.size * LINE_HEIGHT + 20f
                    canvas.drawRect(MARGIN, yPosition - 5f, PAGE_WIDTH - MARGIN, yPosition + alturaObs, paintFondoObs)

                    yPosition += 10f
                    lineas.forEach { linea ->
                        canvas.drawText(linea, MARGIN + 10f, yPosition, paintTextoSecundario)
                        yPosition += LINE_HEIGHT
                    }

                    yPosition += 20f
                }

                // ===== ESPACIO PARA FIRMA =====
                yPosition = maxOf(yPosition, PAGE_HEIGHT - 180f) // Asegurar que quede al final

                canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, paintLinea)
                yPosition += 25f

                canvas.drawText("FIRMA DEL MÉDICO", MARGIN, yPosition, paintSubtitulo)
                yPosition += 35f

                // Línea para firma
                val paintLineaFirma = Paint().apply {
                    color = Color.parseColor("#666666")
                    strokeWidth = 1f
                    pathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                }
                canvas.drawLine(MARGIN + 50f, yPosition, MARGIN + 250f, yPosition, paintLineaFirma)

                yPosition += 18f
                canvas.drawText("Dr(a). ${receta.medico.nombre}", MARGIN + 80f, yPosition, paintTextoSecundario)

                // ===== FOOTER =====
                val footerY = PAGE_HEIGHT - 60f
                canvas.drawLine(MARGIN, footerY, PAGE_WIDTH - MARGIN, footerY, paintLinea)

                val paintFooter = Paint().apply {
                    color = Color.parseColor("#999999")
                    textSize = 9f
                }

                val fechaGeneracion = SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm", Locale.getDefault()).format(Date())
                canvas.drawText("Documento generado automáticamente por SmartFlow Medical el $fechaGeneracion",
                    MARGIN, footerY + 15f, paintFooter)

                canvas.drawText("Este documento tiene validez oficial con la firma del médico tratante",
                    MARGIN, footerY + 28f, paintFooter)

                pdfDocument.finishPage(page)

                // Guardar PDF
                val file = guardarPDF(context, pdfDocument, receta)
                pdfDocument.close()

                return file

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
                return null
            }
        }

        private fun guardarPDF(context: Context, pdfDocument: PdfDocument, receta: Receta): File? {
            val fileName = "Receta_${receta.medico.nombre.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                guardarPDFMediaStore(context, pdfDocument, fileName)
            } else {
                guardarPDFLegacy(context, pdfDocument, fileName)
            }
        }

        private fun guardarPDFMediaStore(context: Context, pdfDocument: PdfDocument, fileName: String): File? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = android.content.ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/SmartFlow")
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }

                    val projection = arrayOf(MediaStore.MediaColumns.DATA)
                    resolver.query(it, projection, null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                            return File(cursor.getString(columnIndex))
                        }
                    }
                }
            }
            return null
        }

        private fun guardarPDFLegacy(context: Context, pdfDocument: PdfDocument, fileName: String): File {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val smartFlowDir = File(downloadsDir, "SmartFlow")

            if (!smartFlowDir.exists()) {
                smartFlowDir.mkdirs()
            }

            val file = File(smartFlowDir, fileName)
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }

            return file
        }

        fun abrirPDF(context: Context, file: File) {
            try {
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                } else {
                    Uri.fromFile(file)
                }

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

                context.startActivity(Intent.createChooser(intent, "Abrir PDF con"))
            } catch (e: Exception) {
                Toast.makeText(context, "No se puede abrir el PDF: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        private fun formatearFecha(fechaISO: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val fecha = inputFormat.parse(fechaISO)

                val outputFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
                fecha?.let { outputFormat.format(it) } ?: fechaISO
            } catch (e: Exception) {
                fechaISO
            }
        }

        private fun dividirTextoEnLineas(texto: String, anchoMaximo: Float, paint: Paint): List<String> {
            val palabras = texto.split(" ")
            val lineas = mutableListOf<String>()
            var lineaActual = ""

            palabras.forEach { palabra ->
                val lineaPrueba = if (lineaActual.isEmpty()) palabra else "$lineaActual $palabra"
                val ancho = paint.measureText(lineaPrueba)

                if (ancho <= anchoMaximo) {
                    lineaActual = lineaPrueba
                } else {
                    if (lineaActual.isNotEmpty()) {
                        lineas.add(lineaActual)
                    }
                    lineaActual = palabra
                }
            }

            if (lineaActual.isNotEmpty()) {
                lineas.add(lineaActual)
            }

            return lineas
        }
    }
}