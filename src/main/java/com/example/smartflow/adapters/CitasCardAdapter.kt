package com.example.smartflow.adapters

import android.graphics.drawable.GradientDrawable
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
import com.example.smartflow.data.models.Cita
import java.text.SimpleDateFormat
import java.util.*

class CitasCardAdapter(
    private var citas: MutableList<Cita>,
    private val onCitaClick: (Cita) -> Unit
) : RecyclerView.Adapter<CitasCardAdapter.CitaCardViewHolder>() {

    inner class CitaCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardCita: CardView = view.findViewById(R.id.card_cita)
        val llBackground: View = view.findViewById(R.id.ll_cita_background)
        val tvNumeroCita: TextView = view.findViewById(R.id.tv_numero_cita)
        val tvEstadoBadge: TextView = view.findViewById(R.id.tv_estado_badge)
        val ivDoctorAvatar: ImageView = view.findViewById(R.id.iv_doctor_avatar)
        val tvDoctorNombre: TextView = view.findViewById(R.id.tv_doctor_nombre)
        val tvEspecialidad: TextView = view.findViewById(R.id.tv_especialidad)
        val tvFecha: TextView = view.findViewById(R.id.tv_fecha)
        val tvHora: TextView = view.findViewById(R.id.tv_hora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cita_card, parent, false)
        return CitaCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitaCardViewHolder, position: Int) {
        val cita = citas[position]

        // Aplicar gradiente azul para las citas
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                android.graphics.Color.parseColor("#2A6FB0"),
                android.graphics.Color.parseColor("#1E5A8C")
            )
        )
        gradient.cornerRadius = 48f // 16dp * 3
        holder.llBackground.background = gradient

        // Número de cita
        holder.tvNumeroCita.text = "Cita #${(position + 1).toString().padStart(3, '0')}"

        // Estado
        holder.tvEstadoBadge.text = when (cita.estado) {
            "confirmada" -> "Confirmada"
            "pendiente" -> "Pendiente"
            "completada" -> "Completada"
            else -> cita.estado
        }

        // Color del badge según estado
        val badgeColor = when (cita.estado) {
            "confirmada" -> android.graphics.Color.parseColor("#4CAF50")
            "pendiente" -> android.graphics.Color.parseColor("#FF9800")
            "completada" -> android.graphics.Color.parseColor("#2196F3")
            else -> android.graphics.Color.parseColor("#9E9E9E")
        }
        val badgeBg = GradientDrawable()
        badgeBg.setColor(badgeColor)
        badgeBg.cornerRadius = 36f
        holder.tvEstadoBadge.background = badgeBg

        // Doctor
        holder.tvDoctorNombre.text = "Dr. ${cita.medico.nombre}"
        holder.tvEspecialidad.text = cita.medico.especialidad

        // Foto del doctor
        if (!cita.medico.foto.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(cita.medico.foto)
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.ivDoctorAvatar)
        }

        // Formatear fecha
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(cita.fecha)
            holder.tvFecha.text = outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            holder.tvFecha.text = cita.fecha.substring(0, 10)
        }

        // Hora
        holder.tvHora.text = cita.hora

        // Click en la tarjeta
        holder.cardCita.setOnClickListener {
            onCitaClick(cita)
        }
    }

    override fun getItemCount() = citas.size

    // ✅ Método original - Reemplaza toda la lista
    fun updateCitas(newCitas: List<Cita>) {
        citas.clear()
        citas.addAll(newCitas)
        notifyDataSetChanged()
    }

    // ✅ NUEVOS MÉTODOS PARA ACTUALIZACIÓN INTELIGENTE

    // Agregar cita al inicio (sin recargar todo)
    fun agregarCita(cita: Cita) {
        citas.add(0, cita)
        notifyItemInserted(0)
    }

    // Actualizar cita por posición
    fun actualizarCita(position: Int, cita: Cita) {
        if (position in citas.indices) {
            citas[position] = cita
            notifyItemChanged(position)
        }
    }

    // Actualizar cita por ID
    fun actualizarCitaPorId(citaId: String, citaActualizada: Cita) {
        val index = citas.indexOfFirst { it._id == citaId }
        if (index != -1) {
            citas[index] = citaActualizada
            notifyItemChanged(index)
        }
    }

    // Eliminar cita por posición
    fun eliminarCita(position: Int) {
        if (position in citas.indices) {
            citas.removeAt(position)
            notifyItemRemoved(position)
            // Actualizar números de citas restantes
            notifyItemRangeChanged(position, citas.size - position)
        }
    }

    // Eliminar cita por ID
    fun eliminarCitaPorId(citaId: String) {
        val index = citas.indexOfFirst { it._id == citaId }
        if (index != -1) {
            citas.removeAt(index)
            notifyItemRemoved(index)
            // Actualizar números de citas restantes
            notifyItemRangeChanged(index, citas.size - index)
        }
    }

    // Obtener lista actual de citas
    fun getCitasActuales(): List<Cita> {
        return citas.toList()
    }

    // Verificar si una cita ya existe
    fun existeCita(citaId: String): Boolean {
        return citas.any { it._id == citaId }
    }
}