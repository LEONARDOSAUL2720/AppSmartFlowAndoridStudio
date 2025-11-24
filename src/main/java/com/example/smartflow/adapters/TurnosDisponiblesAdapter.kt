package com.example.smartflow.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.smartflow.R
import com.example.smartflow.data.models.MedicoConHorariosDisponibles
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class TurnosDisponiblesAdapter(
    private var turnos: List<MedicoConHorariosDisponibles>,
    private val onHorarioClick: (medicoId: String, medicoNombre: String, especialidad: String, hora: String) -> Unit
) : RecyclerView.Adapter<TurnosDisponiblesAdapter.ViewHolder>() {

    // 🎨 Paleta de gradientes (MISMOS COLORES que tus cards rojas/moradas)
    private val gradientes = listOf(
        Pair("#2A6FB0", "#1E5A8C"), // Azul
        Pair("#E74C3C", "#C0392B"), // Rojo (como Cardiología)
        Pair("#9B59B6", "#8E44AD"), // Morado (como Pediatría)
        Pair("#00A9B7", "#008C99"), // Turquesa
        Pair("#57B894", "#3D9577")  // Verde
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llBackground: LinearLayout = view.findViewById(R.id.ll_turno_background)
        val ivMedico: ImageView = view.findViewById(R.id.iv_medico_turno)
        val tvNombreMedico: TextView = view.findViewById(R.id.tv_nombre_medico_turno)
        val tvEspecialidades: TextView = view.findViewById(R.id.tv_especialidades_turno)
        val tvHorarioTrabajo: TextView = view.findViewById(R.id.tv_horario_trabajo)
        val chipGroupHorarios: ChipGroup = view.findViewById(R.id.chip_group_horarios_turno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_turno_disponible, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val turno = turnos[position]
        val medico = turno.medico

        // 🎨 Aplicar gradiente alternado
        val (startColorHex, endColorHex) = gradientes[position % gradientes.size]
        val startColor = Color.parseColor(startColorHex)
        val endColor = Color.parseColor(endColorHex)

        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(startColor, endColor)
        )
        gradient.cornerRadius = 48f
        holder.llBackground.background = gradient

        // Foto del médico
        Glide.with(holder.itemView.context)
            .load(medico.foto)
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_user_placeholder)
            .error(R.drawable.ic_user_placeholder)
            .into(holder.ivMedico)

        // Nombre
        holder.tvNombreMedico.text = medico.nombre

        // Especialidades
        val especialidades = medico.especialidades.joinToString(", ") { it.nombre }
        holder.tvEspecialidades.text = especialidades

        // Horario de trabajo
        holder.tvHorarioTrabajo.text = "Horario: ${turno.horarioTrabajo.horaInicio} - ${turno.horarioTrabajo.horaFin}"

        // Limpiar chips
        holder.chipGroupHorarios.removeAllViews()

        // Agregar chips blancos
        turno.horariosDisponibles.forEach { hora ->
            val chip = Chip(holder.itemView.context).apply {
                text = hora
                setChipBackgroundColorResource(android.R.color.white)
                setTextColor(startColor)
                isClickable = true
                isCheckable = false
                chipCornerRadius = 24f
                textSize = 14f
                setPadding(24, 12, 24, 12)
                setOnClickListener {
                    val especialidadPrincipal = medico.especialidades.firstOrNull()?.nombre ?: ""
                    onHorarioClick(medico._id, medico.nombre, especialidadPrincipal, hora)
                }
            }
            holder.chipGroupHorarios.addView(chip)
        }
    }

    override fun getItemCount() = turnos.size

    fun updateTurnos(nuevosTurnos: List<MedicoConHorariosDisponibles>) {
        turnos = nuevosTurnos
        notifyDataSetChanged()
    }
}