
package com.example.smartflow.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.smartflow.R
import com.example.smartflow.data.models.MedicoData


class MedicosGenerarCitaAdapter(
    private var medicos: List<MedicoData>,
    private val onMedicoClick: (MedicoData) -> Unit
) : RecyclerView.Adapter<MedicosGenerarCitaAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardMedico: MaterialCardView = view.findViewById(R.id.card_medico)
        val ivFoto: ImageView = view.findViewById(R.id.iv_medico_foto)
        val tvNombre: TextView = view.findViewById(R.id.tv_medico_nombre)
        val tvEspecialidad: TextView = view.findViewById(R.id.tv_medico_especialidad)
        val tvTarifa: TextView = view.findViewById(R.id.tv_medico_tarifa)
        val tvCalificacion: TextView = view.findViewById(R.id.tv_medico_calificacion)
        val tvConsultas: TextView = view.findViewById(R.id.tv_medico_consultas)
        val viewColorBar: View = view.findViewById(R.id.view_color_bar)

        fun bind(medico: MedicoData, isSelected: Boolean) {
            // Foto del médico
            ivFoto.load(medico.foto) {
                crossfade(true)
                placeholder(R.drawable.ic_user_placeholder)
                transformations(CircleCropTransformation())
            }
            // Nombre
            tvNombre.text = "Dr. ${medico.nombre ?: "-"} ${medico.apellido ?: ""}"
            // Especialidad: no disponible en la respuesta, se puede dejar vacío o mostrar "-"
            tvEspecialidad.text = "-"
            // Tarifa
            tvTarifa.text = "$${medico.medicoInfo?.tarifaConsulta ?: 0}"
            // Calificación y consultas
            tvCalificacion.text = String.format("%.1f", medico.medicoInfo?.calificacionPromedio ?: 0.0)
            tvConsultas.text = "${medico.medicoInfo?.totalCitasAtendidas ?: 0} consultas"
            // Barra de color: fija o gris porque no hay código de especialidad
            viewColorBar.setBackgroundColor(0xFF333333.toInt())
            // Fondo visual para selección
            cardMedico.isSelected = isSelected
            cardMedico.setCardBackgroundColor(
                if (isSelected) 0xFFE3F2FD.toInt() /* azul claro */ else 0xFFFFFFFF.toInt()
            )
            cardMedico.strokeWidth = if (isSelected) 6 else 0
            cardMedico.strokeColor = if (isSelected) 0xFF1976D2.toInt() else 0x00000000
            cardMedico.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onMedicoClick(medico)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medico_generar_cita, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medico = medicos[position]
        holder.bind(medico, position == selectedPosition)
    }

    override fun getItemCount() = medicos.size

    fun updateMedicos(nuevosMedicos: List<MedicoData>) {
        medicos = nuevosMedicos
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    fun getSelectedMedico(): MedicoData? =
        if (selectedPosition != RecyclerView.NO_POSITION) medicos[selectedPosition] else null

    // Ya no se usa getColorForEspecialidad porque no hay código en la respuesta
}
