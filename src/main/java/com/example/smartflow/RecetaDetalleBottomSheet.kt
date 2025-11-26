package com.example.smartflow

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.smartflow.data.models.Medicamento
import com.example.smartflow.data.models.Receta
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class RecetaDetalleBottomSheet : BottomSheetDialogFragment() {

    private lateinit var receta: Receta

    companion object {
        private const val ARG_RECETA_ID = "receta_id"
        private const val ARG_RECETA_FECHA = "receta_fecha"
        private const val ARG_MEDICO_NOMBRE = "medico_nombre"
        private const val ARG_MEDICO_ESPECIALIDAD = "medico_especialidad"
        private const val ARG_MEDICO_CEDULA = "medico_cedula"
        private const val ARG_MEDICO_FOTO = "medico_foto"
        private const val ARG_CITA_HORA = "cita_hora"
        private const val ARG_OBSERVACIONES = "observaciones"
        private const val ARG_PDF_URL = "pdf_url"
        private const val ARG_QR_CODE = "qr_code"
        private const val ARG_MEDICAMENTOS_JSON = "medicamentos_json"

        fun newInstance(receta: Receta): RecetaDetalleBottomSheet {
            val fragment = RecetaDetalleBottomSheet()
            val args = Bundle()

            // Pasar datos de la receta
            args.putString(ARG_RECETA_ID, receta.id)
            args.putString(ARG_RECETA_FECHA, receta.fecha)
            args.putString(ARG_MEDICO_NOMBRE, receta.medico.nombre)
            args.putString(ARG_MEDICO_ESPECIALIDAD, receta.medico.especialidad)
            args.putString(ARG_MEDICO_CEDULA, receta.medico.cedulaProfesional)
            args.putString(ARG_MEDICO_FOTO, receta.medico.foto)
            args.putString(ARG_CITA_HORA, receta.cita.hora)
            args.putString(ARG_OBSERVACIONES, receta.observaciones)
            args.putString(ARG_PDF_URL, receta.pdfUrl)
            args.putString(ARG_QR_CODE, receta.qrCode)

            // Convertir lista de medicamentos a JSON simple
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
        val medicoNombre = arguments?.getString(ARG_MEDICO_NOMBRE) ?: ""
        val medicoEspecialidad = arguments?.getString(ARG_MEDICO_ESPECIALIDAD) ?: ""
        val medicoCedula = arguments?.getString(ARG_MEDICO_CEDULA)
        val medicoFoto = arguments?.getString(ARG_MEDICO_FOTO)
        val fecha = arguments?.getString(ARG_RECETA_FECHA) ?: ""
        val hora = arguments?.getString(ARG_CITA_HORA) ?: ""
        val observaciones = arguments?.getString(ARG_OBSERVACIONES)
        val pdfUrl = arguments?.getString(ARG_PDF_URL)
        val qrCode = arguments?.getString(ARG_QR_CODE)
        val medicamentosString = arguments?.getString(ARG_MEDICAMENTOS_JSON) ?: ""

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

        // Llenar información del médico
        tvMedicoNombre.text = medicoNombre
        tvEspecialidad.text = medicoEspecialidad

        if (medicoCedula != null) {
            tvCedula.text = "Cédula: $medicoCedula"
            tvCedula.visibility = View.VISIBLE
        } else {
            tvCedula.visibility = View.GONE
        }

        // Foto del médico
        ivMedicoFoto.load(medicoFoto) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_foreground)
            error(R.drawable.ic_launcher_foreground)
            transformations(CircleCropTransformation())
        }

        // Fecha y hora
        tvFecha.text = formatearFecha(fecha)
        tvHora.text = hora

        // Medicamentos
        val medicamentos = parsearMedicamentos(medicamentosString)
        llenarMedicamentos(llMedicamentosLista, medicamentos)

        // Observaciones
        if (observaciones.isNullOrEmpty()) {
            cardObservaciones.visibility = View.GONE
        } else {
            tvObservaciones.text = observaciones
            cardObservaciones.visibility = View.VISIBLE
        }

        // Botón PDF
        if (pdfUrl.isNullOrEmpty()) {
            btnDescargarPdf.visibility = View.GONE
        } else {
            btnDescargarPdf.setOnClickListener {
                abrirPDF(pdfUrl)
            }
        }

        // Botón QR
        if (qrCode.isNullOrEmpty()) {
            btnVerQr.visibility = View.GONE
        } else {
            btnVerQr.visibility = View.VISIBLE
            btnVerQr.setOnClickListener {
                Toast.makeText(requireContext(), "Ver código QR (próximamente)", Toast.LENGTH_SHORT).show()
            }
        }
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

            // Ocultar separador en el último item
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

    private fun abrirPDF(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(url), "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No se puede abrir el PDF", Toast.LENGTH_SHORT).show()
        }
    }
}