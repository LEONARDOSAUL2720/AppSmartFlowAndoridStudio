package com.example.smartflow.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.smartflow.R
import com.example.smartflow.data.models.MedicoHorarios

class HorariosDisponiblesAdapter(
    private var medicosHorarios: List<MedicoHorarios>,
    private val onHorarioClick: (String, String) -> Unit // medicoId, horario
) : RecyclerView.Adapter<HorariosDisponiblesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardMedico: CardView = view.findViewById(R.id.card_medico_horario)
        val ivMedico: ImageView = view.findViewById(R.id.iv_medico_horario)
        val tvMedico: TextView = view.findViewById(R.id.tv_medico_horario)
        val tvEspecialidades: TextView = view.findViewById(R.id.tv_especialidades_horario)
        val tvHorario: TextView = view.findViewById(R.id.tv_horario_disponible)
        val tvTurnosEspera: TextView = view.findViewById(R.id.tv_turnos_espera)
        val tvTiempoEstimado: TextView = view.findViewById(R.id.tv_tiempo_estimado)
        val tvDisponibilidad: TextView = view.findViewById(R.id.tv_disponibilidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_horario_disponible, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicoHorarios = medicosHorarios[position]
        val medico = medicoHorarios.medico
        val horario = medicoHorarios.horarios.firstOrNull() ?: return
        
        // Foto médico
        Glide.with(holder.itemView.context)
            .load(medico.foto)
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_user_placeholder)
            .into(holder.ivMedico)
        
        // Datos médico
        holder.tvMedico.text = medico.nombre
        
        val especialidades = medico.especialidades.joinToString(", ") { it.nombre }
        holder.tvEspecialidades.text = especialidades
        
        // Horario
        holder.tvHorario.text = "${horario.horaInicio} - ${horario.horaFin}"
        holder.tvTurnosEspera.text = "${horario.turnosEnEspera} personas en espera"
        holder.tvTiempoEstimado.text = "~${horario.tiempoEstimadoMin} min"
        
        // Disponibilidad
        if (horario.disponible) {
            holder.tvDisponibilidad.text = "✅ Disponible"
            holder.tvDisponibilidad.setTextColor(Color.parseColor("#4CAF50"))
            holder.cardMedico.isEnabled = true
            holder.cardMedico.alpha = 1.0f
        } else {
            holder.tvDisponibilidad.text = "🔴 Lleno"
            holder.tvDisponibilidad.setTextColor(Color.parseColor("#F44336"))
            holder.cardMedico.isEnabled = false
            holder.cardMedico.alpha = 0.6f
        }
        
        // Click
        holder.cardMedico.setOnClickListener {
            if (horario.disponible) {
                onHorarioClick(medico._id, "${horario.horaInicio}-${horario.horaFin}")
            }
        }
    }

    override fun getItemCount() = medicosHorarios.size

    fun updateHorarios(nuevosHorarios: List<MedicoHorarios>) {
        medicosHorarios = nuevosHorarios
        notifyDataSetChanged()
    }
}
