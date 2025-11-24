package com.example.smartflow.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.smartflow.R
import com.example.smartflow.data.models.CitaHoy
import java.text.SimpleDateFormat
import java.util.Locale

class FilaVirtualAdapter(
    private var citas: List<CitaHoy>
) : RecyclerView.Adapter<FilaVirtualAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumeroTurno: TextView = view.findViewById(R.id.tv_numero_turno)
        val tvHoraCita: TextView = view.findViewById(R.id.tv_hora_cita)
        val tvFechaCita: TextView = view.findViewById(R.id.tv_fecha_cita)
        val tvEstadoCita: TextView = view.findViewById(R.id.tv_estado_cita)
        val ivMedico: ImageView = view.findViewById(R.id.iv_medico_fila)
        val tvMedicoNombre: TextView = view.findViewById(R.id.tv_medico_nombre)
        val tvEspecialidad: TextView = view.findViewById(R.id.tv_especialidad)
        val viewCodigoEspecialidad: View = view.findViewById(R.id.view_codigo_especialidad)
        val ivPaciente: ImageView = view.findViewById(R.id.iv_paciente_fila)
        val tvPacienteNombre: TextView = view.findViewById(R.id.tv_paciente_nombre)
        val tvMotivoCita: TextView = view.findViewById(R.id.tv_motivo_cita)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cita_fila_virtual, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cita = citas[position]
        
        // Número de turno (posición + 1)
        holder.tvNumeroTurno.text = (position + 1).toString()
        
        // Hora y fecha
        holder.tvHoraCita.text = cita.hora
        
        // Fecha formateada (asumiendo que viene en el response o usar fecha actual)
        val sdf = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
        holder.tvFechaCita.text = sdf.format(java.util.Date())
        
        // Estado
        holder.tvEstadoCita.text = cita.estado.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
        
        when (cita.estado.lowercase()) {
            "completada" -> {
                holder.tvEstadoCita.setBackgroundResource(R.drawable.bg_estado_completada)
                holder.tvEstadoCita.setTextColor(Color.WHITE)
            }
            "confirmada" -> {
                holder.tvEstadoCita.setBackgroundResource(R.drawable.bg_estado_confirmada)
                holder.tvEstadoCita.setTextColor(Color.WHITE)
            }
            "pendiente" -> {
                holder.tvEstadoCita.setBackgroundResource(R.drawable.bg_estado_pendiente)
                holder.tvEstadoCita.setTextColor(Color.WHITE)
            }
            else -> {
                holder.tvEstadoCita.setBackgroundColor(Color.parseColor("#9E9E9E"))
                holder.tvEstadoCita.setTextColor(Color.WHITE)
            }
        }
        
        // Médico
        Glide.with(holder.itemView.context)
            .load(cita.medico.foto)
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_user_placeholder)
            .into(holder.ivMedico)
        
        holder.tvMedicoNombre.text = cita.medico.nombre
        holder.tvEspecialidad.text = cita.medico.especialidad
        
        // Color por código de especialidad
        val colorEspecialidad = when (cita.medico.especialidadCodigo) {
            "A" -> "#F44336" // Rojo - Cardiología
            "B" -> "#2196F3" // Azul - Dermatología
            "C" -> "#9C27B0" // Púrpura - Pediatría
            "M" -> "#4CAF50" // Verde - Medicina General
            else -> "#9E9E9E" // Gris por defecto
        }
        holder.viewCodigoEspecialidad.setBackgroundColor(Color.parseColor(colorEspecialidad))
        
        // Paciente
        Glide.with(holder.itemView.context)
            .load(cita.paciente.foto)
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_user_placeholder)
            .into(holder.ivPaciente)
        
        holder.tvPacienteNombre.text = cita.paciente.nombre
        
        // Motivo
        holder.tvMotivoCita.text = "Motivo: ${cita.motivo}"
    }

    override fun getItemCount() = citas.size

    fun updateCitas(nuevasCitas: List<CitaHoy>) {
        citas = nuevasCitas
        notifyDataSetChanged()
    }
}
