package com.example.smartflow.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.smartflow.R
import com.example.smartflow.data.models.Receta
import java.text.SimpleDateFormat
import java.util.*

class RecetasAdapter(
    private var recetas: List<Receta>,
    private val onVerDetallesClick: (Receta) -> Unit
) : RecyclerView.Adapter<RecetasAdapter.RecetaViewHolder>() {

    inner class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivMedicoFoto: ImageView = itemView.findViewById(R.id.iv_medico_foto)
        val tvMedicoNombre: TextView = itemView.findViewById(R.id.tv_medico_nombre)
        val tvEspecialidad: TextView = itemView.findViewById(R.id.tv_especialidad)
        val tvFecha: TextView = itemView.findViewById(R.id.tv_fecha)
        val tvHora: TextView = itemView.findViewById(R.id.tv_hora)
        val tvCantidadMedicamentos: TextView = itemView.findViewById(R.id.tv_cantidad_medicamentos)
        val btnVerDetalles: Button = itemView.findViewById(R.id.btn_ver_detalles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receta_card, parent, false)
        return RecetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        val receta = recetas[position]

        // Nombre del médico
        holder.tvMedicoNombre.text = receta.medico.nombre

        // Especialidad
        holder.tvEspecialidad.text = receta.medico.especialidad

        // Fecha formateada (ej: "15 Noviembre 2024")
        holder.tvFecha.text = formatearFecha(receta.fecha)

        // Hora formateada (ej: "10:30 AM")
        holder.tvHora.text = receta.cita.hora

        // Cantidad de medicamentos
        val cantidadMedicamentos = receta.medicamentos.size
        holder.tvCantidadMedicamentos.text = if (cantidadMedicamentos == 1) {
            "1 medicamento prescrito"
        } else {
            "$cantidadMedicamentos medicamentos prescritos"
        }

        // Foto del médico con Coil
        holder.ivMedicoFoto.load(receta.medico.foto) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_foreground)
            error(R.drawable.ic_launcher_foreground)
            transformations(CircleCropTransformation())
        }

        // Click en "Ver Detalles"
        holder.btnVerDetalles.setOnClickListener {
            onVerDetallesClick(receta)
        }
    }

    override fun getItemCount(): Int = recetas.size

    // Actualizar lista de recetas
    fun updateRecetas(nuevasRecetas: List<Receta>) {
        this.recetas = nuevasRecetas
        notifyDataSetChanged()
    }

    // Formatear fecha de ISO 8601 a formato legible
    private fun formatearFecha(fechaISO: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val fecha = inputFormat.parse(fechaISO)

            val outputFormat = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
            fecha?.let { outputFormat.format(it) } ?: fechaISO
        } catch (e: Exception) {
            // Si hay error, intentar formato alternativo sin milisegundos
            try {
                val inputFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                inputFormat2.timeZone = TimeZone.getTimeZone("UTC")
                val fecha = inputFormat2.parse(fechaISO)

                val outputFormat = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
                fecha?.let { outputFormat.format(it) } ?: fechaISO
            } catch (e2: Exception) {
                fechaISO // Devolver el string original si falla
            }
        }
    }
}