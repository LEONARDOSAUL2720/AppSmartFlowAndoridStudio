package com.example.smartflow.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartflow.R
import com.example.smartflow.data.models.EspecialidadData

class EspecialidadesAdapter(
    private var especialidades: List<EspecialidadData>,
    private val onEspecialidadClick: (EspecialidadData) -> Unit
) : RecyclerView.Adapter<EspecialidadesAdapter.ViewHolder>() {

    private var selectedIndex: Int = RecyclerView.NO_POSITION

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardEspecialidad: CardView = view.findViewById(R.id.card_especialidad)
        val layoutEspecialidad: android.widget.LinearLayout = view.findViewById(R.id.layout_especialidad)
        val tvNombre: TextView = view.findViewById(R.id.tv_especialidad_nombre)
        val tvDescripcion: TextView = view.findViewById(R.id.tv_especialidad_descripcion)
        val tvIcono: TextView = view.findViewById(R.id.tv_especialidad_icono)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_especialidad_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val especialidad = especialidades[holder.adapterPosition]
        holder.tvNombre.text = especialidad.nombre
        holder.tvDescripcion.text = especialidad.descripcion

        // Asignar ícono según especialidad (solo front)
        holder.tvIcono.text = when (especialidad.nombre.lowercase()) {
            "cardiología" -> "\uD83D\uDC89" // Jeringa
            "dermatología" -> "\uD83D\uDC84" // Labial
            "medicina general" -> "\uD83C\uDFE5" // Hospital
            "pediatría" -> "\uD83D\uDC76" // Bebé
            else -> "\uD83D\uDCDA" // Libro
        }

        // Cambiar fondo completo del LinearLayout según selección
        if (holder.adapterPosition == selectedIndex) {
            holder.layoutEspecialidad.setBackgroundColor(android.graphics.Color.parseColor("#57B894"))
            holder.tvNombre.setTextColor(android.graphics.Color.parseColor("#333333"))
            holder.tvDescripcion.setTextColor(android.graphics.Color.parseColor("#333333"))
            holder.tvIcono.setTextColor(android.graphics.Color.parseColor("#333333"))
        } else {
            holder.layoutEspecialidad.setBackgroundColor(android.graphics.Color.WHITE)
            holder.tvNombre.setTextColor(android.graphics.Color.parseColor("#333333"))
            holder.tvDescripcion.setTextColor(android.graphics.Color.parseColor("#333333"))
            holder.tvIcono.setTextColor(android.graphics.Color.parseColor("#2A6FB0"))
        }

        holder.cardEspecialidad.setOnClickListener {
            val previousIndex = selectedIndex
            selectedIndex = holder.adapterPosition
            notifyItemChanged(previousIndex)
            notifyItemChanged(selectedIndex)
            onEspecialidadClick(especialidad)
        }
    }

    override fun getItemCount() = especialidades.size

    fun updateEspecialidades(nuevas: List<EspecialidadData>) {
        especialidades = nuevas
        selectedIndex = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}
