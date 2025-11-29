package com.example.smartflow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.smartflow.data.api.RetrofitClient
import com.example.smartflow.data.models.*
import com.example.smartflow.utils.PdfGenerator
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RecetaDetalleBottomSheet : BottomSheetDialogFragment() {

    private lateinit var receta: Receta
    private var calificacionSeleccionada = 0
    private val estrellas = mutableListOf<ImageView>()

    companion object {
        private const val ARG_RECETA_ID = "receta_id"
        private const val ARG_RECETA_FECHA = "receta_fecha"
        private const val ARG_MEDICO_ID = "medico_id"
        private const val ARG_MEDICO_NOMBRE = "medico_nombre"
        private const val ARG_MEDICO_EMAIL = "medico_email"
        private const val ARG_MEDICO_ESPECIALIDAD = "medico_especialidad"
        private const val ARG_MEDICO_CEDULA = "medico_cedula"
        private const val ARG_MEDICO_FOTO = "medico_foto"
        private const val ARG_CITA_ID = "cita_id"
        private const val ARG_CITA_FECHA = "cita_fecha"
        private const val ARG_CITA_HORA = "cita_hora"
        private const val ARG_CITA_ESTADO = "cita_estado"
        private const val ARG_OBSERVACIONES = "observaciones"
        private const val ARG_PDF_URL = "pdf_url"
        private const val ARG_QR_CODE = "qr_code"
        private const val ARG_MEDICAMENTOS_JSON = "medicamentos_json"

        fun newInstance(receta: Receta): RecetaDetalleBottomSheet {
            val fragment = RecetaDetalleBottomSheet()
            val args = Bundle()

            args.putString(ARG_RECETA_ID, receta.id)
            args.putString(ARG_RECETA_FECHA, receta.fecha)

            args.putString(ARG_MEDICO_ID, receta.medico.id)
            args.putString(ARG_MEDICO_NOMBRE, receta.medico.nombre)
            args.putString(ARG_MEDICO_EMAIL, receta.medico.email)
            args.putString(ARG_MEDICO_ESPECIALIDAD, receta.medico.especialidad)
            args.putString(ARG_MEDICO_CEDULA, receta.medico.cedulaProfesional)
            args.putString(ARG_MEDICO_FOTO, receta.medico.foto)

            args.putString(ARG_CITA_ID, receta.cita.id)
            args.putString(ARG_CITA_FECHA, receta.cita.fecha)
            args.putString(ARG_CITA_HORA, receta.cita.hora)
            args.putString(ARG_CITA_ESTADO, receta.cita.estado)

            args.putString(ARG_OBSERVACIONES, receta.observaciones)
            args.putString(ARG_PDF_URL, receta.pdfUrl)
            args.putString(ARG_QR_CODE, receta.qrCode)

            val medicamentosString = receta.medicamentos.joinToString("|||") { med ->
                "${med.nombre}::${med.dosis}::${med.frecuencia}::${med.duracion}"
            }
            args.putString(ARG_MEDICAMENTOS_JSON, medicamentosString)

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_receta_detalle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener datos de los argumentos
        val recetaId = arguments?.getString(ARG_RECETA_ID) ?: ""
        val recetaFecha = arguments?.getString(ARG_RECETA_FECHA) ?: ""

        val medicoId = arguments?.getString(ARG_MEDICO_ID) ?: ""
        val medicoNombre = arguments?.getString(ARG_MEDICO_NOMBRE) ?: ""
        val medicoEmail = arguments?.getString(ARG_MEDICO_EMAIL) ?: ""
        val medicoEspecialidad = arguments?.getString(ARG_MEDICO_ESPECIALIDAD) ?: ""
        val medicoCedula = arguments?.getString(ARG_MEDICO_CEDULA)
        val medicoFoto = arguments?.getString(ARG_MEDICO_FOTO)

        val citaId = arguments?.getString(ARG_CITA_ID) ?: ""
        val citaFecha = arguments?.getString(ARG_CITA_FECHA) ?: ""
        val citaHora = arguments?.getString(ARG_CITA_HORA) ?: ""
        val citaEstado = arguments?.getString(ARG_CITA_ESTADO) ?: ""

        val observaciones = arguments?.getString(ARG_OBSERVACIONES)
        val pdfUrl = arguments?.getString(ARG_PDF_URL)
        val qrCode = arguments?.getString(ARG_QR_CODE)
        val medicamentosString = arguments?.getString(ARG_MEDICAMENTOS_JSON) ?: ""

        // Reconstruir el objeto Receta completo
        val medicamentos = parsearMedicamentos(medicamentosString)

        receta = Receta(
            id = recetaId,
            fecha = recetaFecha,
            medicamentos = medicamentos,
            observaciones = observaciones,
            pdfUrl = pdfUrl,
            qrCode = qrCode,
            medico = MedicoReceta(
                id = medicoId,
                nombre = medicoNombre,
                email = medicoEmail,
                foto = medicoFoto,
                especialidad = medicoEspecialidad,
                cedulaProfesional = medicoCedula
            ),
            cita = CitaReceta(
                id = citaId,
                fecha = citaFecha,
                hora = citaHora,
                estado = citaEstado
            )
        )

        // Vistas
        val ivMedicoFoto = view.findViewById<ImageView>(R.id.iv_medico_foto_detalle)
        val tvMedicoNombre = view.findViewById<TextView>(R.id.tv_medico_nombre_detalle)
        val tvEspecialidad = view.findViewById<TextView>(R.id.tv_especialidad_detalle)
        val tvCedula = view.findViewById<TextView>(R.id.tv_cedula_detalle)
        val tvFecha = view.findViewById<TextView>(R.id.tv_fecha_detalle)
        val tvHora = view.findViewById<TextView>(R.id.tv_hora_detalle)
        val llMedicamentosLista = view.findViewById<LinearLayout>(R.id.ll_medicamentos_lista)
        val cardObservaciones = view.findViewById<CardView>(R.id.card_observaciones)
        val tvObservaciones = view.findViewById<TextView>(R.id.tv_observaciones)
        val btnDescargarPdf = view.findViewById<Button>(R.id.btn_descargar_pdf)
        val btnVerQr = view.findViewById<Button>(R.id.btn_ver_qr)

        // ⭐ Vistas de calificación
        estrellas.add(view.findViewById(R.id.star_1))
        estrellas.add(view.findViewById(R.id.star_2))
        estrellas.add(view.findViewById(R.id.star_3))
        estrellas.add(view.findViewById(R.id.star_4))
        estrellas.add(view.findViewById(R.id.star_5))

        val etComentario = view.findViewById<EditText>(R.id.et_comentario_calificacion)
        val btnEnviarCalificacion = view.findViewById<Button>(R.id.btn_enviar_calificacion)

        // Llenar información del médico
        tvMedicoNombre.text = medicoNombre
        tvEspecialidad.text = medicoEspecialidad

        if (medicoCedula != null) {
            tvCedula.text = "Cédula: $medicoCedula"
            tvCedula.visibility = View.VISIBLE
        } else {
            tvCedula.visibility = View.GONE
        }

        ivMedicoFoto.load(medicoFoto) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_foreground)
            error(R.drawable.ic_launcher_foreground)
            transformations(CircleCropTransformation())
        }

        tvFecha.text = formatearFecha(recetaFecha)
        tvHora.text = citaHora

        llenarMedicamentos(llMedicamentosLista, medicamentos)

        if (observaciones.isNullOrEmpty()) {
            cardObservaciones.visibility = View.GONE
        } else {
            tvObservaciones.text = observaciones
            cardObservaciones.visibility = View.VISIBLE
        }

        // ⭐ Configurar clicks en estrellas
        estrellas.forEachIndexed { index, estrella ->
            estrella.setOnClickListener {
                Log.d("RecetaDetalle", "🖱️ Click en estrella ${index + 1}")
                seleccionarEstrellas(index + 1)
            }
        }

// ✅ AGREGAR ESTE LOG
        Log.d("RecetaDetalle", "🎯 ${estrellas.size} estrellas configuradas correctamente")

        // ✅ Botón enviar calificación
        btnEnviarCalificacion.setOnClickListener {
            enviarCalificacion(medicoId, etComentario.text.toString())
        }

        // Botón PDF
        btnDescargarPdf.setOnClickListener {
            generarYAbrirPDF()
        }

        // Botón QR
        if (qrCode.isNullOrEmpty()) {
            btnVerQr.visibility = View.GONE
        } else {
            btnVerQr.visibility = View.VISIBLE
            btnVerQr.setOnClickListener {
                Toast.makeText(requireContext(), "Ver código QR (próximamente)", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun seleccionarEstrellas(cantidad: Int) {
        Log.d("RecetaDetalle", "⭐⭐⭐ seleccionarEstrellas llamado con cantidad: $cantidad")
        calificacionSeleccionada = cantidad
        Log.d(
            "RecetaDetalle",
            "📊 calificacionSeleccionada actualizada a: $calificacionSeleccionada"
        )

        estrellas.forEachIndexed { index, estrella ->
            if (index < cantidad) {
                // Estrella llena (azul)
                Log.d("RecetaDetalle", "🌟 Estrella ${index + 1}: Cambiando a LLENA (azul)")
                estrella.setImageResource(R.drawable.ic_star_filled)
            } else {
                // Estrella vacía (gris)
                Log.d("RecetaDetalle", "⚪ Estrella ${index + 1}: Cambiando a VACÍA (gris)")
                estrella.setImageResource(R.drawable.ic_star_empty)
            }
        }

        Log.d("RecetaDetalle", "✅ Todas las estrellas actualizadas. Total seleccionadas: $cantidad")
    }

    private fun enviarCalificacion(medicoId: String, comentario: String) {
        if (calificacionSeleccionada == 0) {
            Toast.makeText(
                requireContext(),
                "Por favor selecciona una calificación",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val prefs =
            requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val pacienteId = prefs.getString("user_id", null)

        if (pacienteId == null) {
            Toast.makeText(
                requireContext(),
                "Error: No se encontró el ID del usuario",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // ✅ AGREGAR ESTOS LOGS
        Log.d("RecetaDetalle", "📊 Calificación seleccionada: $calificacionSeleccionada")
        Log.d("RecetaDetalle", "👨‍⚕️ Médico ID: $medicoId")
        Log.d("RecetaDetalle", "🧑 Paciente ID: $pacienteId")
        Log.d("RecetaDetalle", "💬 Comentario: $comentario")
        Log.d(
            "RecetaDetalle",
            "📤 Enviando calificación: $calificacionSeleccionada estrellas al médico $medicoId"
        )

        val request = CalificacionRequest(
            medicoId = medicoId,
            pacienteId = pacienteId,
            calificacion = calificacionSeleccionada,
            comentario = if (comentario.isBlank()) null else comentario
        )

        RetrofitClient.calificacionesApiService.calificarMedico(request)
            .enqueue(object : Callback<CalificacionResponse> {
                override fun onResponse(
                    call: Call<CalificacionResponse>,
                    response: Response<CalificacionResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Log.d("RecetaDetalle", "✅ Calificación enviada exitosamente")
                        Toast.makeText(
                            requireContext(),
                            "¡Gracias por tu calificación!",
                            Toast.LENGTH_LONG
                        ).show()
                        dismiss()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(
                            "RecetaDetalle",
                            "❌ Error en respuesta: ${response.code()}, Body: $errorBody"
                        )

                        // Mostrar mensaje específico si ya calificó antes
                        if (response.code() == 400 && errorBody?.contains("Ya has calificado") == true) {
                            Toast.makeText(
                                requireContext(),
                                "Ya has calificado a este médico anteriormente",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error al enviar calificación",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<CalificacionResponse>, t: Throwable) {
                    Log.e("RecetaDetalle", "❌ Error de red: ${t.message}", t)
                    Toast.makeText(
                        requireContext(),
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun llenarMedicamentos(container: LinearLayout, medicamentos: List<Medicamento>) {
        container.removeAllViews()

        medicamentos.forEachIndexed { index, medicamento ->
            val medicamentoView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_medicamento_detalle, container, false)

            val tvNombre = medicamentoView.findViewById<TextView>(R.id.tv_medicamento_nombre)
            val tvDetalles = medicamentoView.findViewById<TextView>(R.id.tv_medicamento_detalles)
            val separador = medicamentoView.findViewById<View>(R.id.separador_medicamento)

            tvNombre.text = medicamento.nombre
            tvDetalles.text = "• Dosis: ${medicamento.dosis}\n" +
                    "• Frecuencia: ${medicamento.frecuencia}\n" +
                    "• Duración: ${medicamento.duracion}"

            if (index == medicamentos.size - 1) {
                separador.visibility = View.GONE
            }

            container.addView(medicamentoView)
        }
    }

    private fun parsearMedicamentos(medicamentosString: String): List<Medicamento> {
        if (medicamentosString.isEmpty()) return emptyList()

        return medicamentosString.split("|||").mapNotNull { medString ->
            val partes = medString.split("::")
            if (partes.size == 4) {
                Medicamento(
                    nombre = partes[0],
                    dosis = partes[1],
                    frecuencia = partes[2],
                    duracion = partes[3]
                )
            } else null
        }
    }

    private fun formatearFecha(fechaISO: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val fecha = inputFormat.parse(fechaISO)

            val outputFormat = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
            fecha?.let { outputFormat.format(it) } ?: fechaISO
        } catch (e: Exception) {
            fechaISO
        }
    }

    private fun generarYAbrirPDF() {
        Toast.makeText(requireContext(), "Generando PDF...", Toast.LENGTH_SHORT).show()

        try {
            val pdfFile = PdfGenerator.generarRecetaPDF(requireContext(), receta)

            if (pdfFile != null) {
                Toast.makeText(
                    requireContext(),
                    "PDF generado en Descargas/SmartFlow",
                    Toast.LENGTH_LONG
                ).show()
                PdfGenerator.abrirPDF(requireContext(), pdfFile)
            } else {
                Toast.makeText(requireContext(), "Error al generar el PDF", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}