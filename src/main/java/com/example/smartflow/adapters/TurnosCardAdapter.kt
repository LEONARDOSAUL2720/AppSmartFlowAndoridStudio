package com.example.smartflow.adapters

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartflow.R
import com.example.smartflow.data.models.ResumenTurno
import com.example.smartflow.utils.EspecialidadColors

class TurnosCardAdapter(
    private var turnos: List<ResumenTurno>,
    private val onTurnoClick: (ResumenTurno) -> Unit
) : RecyclerView.Adapter<TurnosCardAdapter.TurnoCardViewHolder>() {

    inner class TurnoCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardTurno: CardView = view.findViewById(R.id.card_turno)
        val llBackground: View = view.findViewById(R.id.ll_turno_background)
        val tvCodigoEspecialidad: TextView = view.findViewById(R.id.tv_codigo_especialidad)
        val tvNombreEspecialidad: TextView = view.findViewById(R.id.tv_nombre_especialidad)
        val tvTurnoActual: TextView = view.findViewById(R.id.tv_turno_actual)
        val tvPersonasEspera: TextView = view.findViewById(R.id.tv_personas_espera)
        val tvTiempoEstimado: TextView = view.findViewById(R.id.tv_tiempo_estimado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurnoCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_turno_card, parent, false)
        return TurnoCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: TurnoCardViewHolder, position: Int) {
        val turno = turnos[position]

        // Aplicar gradiente según especialidad
        val (startColor, endColor) = EspecialidadColors.getGradient(turno.especialidad.codigo)
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(startColor, endColor)
        )
        gradient.cornerRadius = 48f // 16dp * 3
        holder.llBackground.background = gradient

        // Código de especialidad
        holder.tvCodigoEspecialidad.text = turno.especialidad.codigo
        holder.tvCodigoEspecialidad.setTextColor(startColor)

        // Nombre de especialidad
        holder.tvNombreEspecialidad.text = turno.especialidad.nombre

        // Turno actual
        if (turno.turnoActual != null) {
            holder.tvTurnoActual.text = "Atendiendo: ${turno.turnoActual}"
            holder.tvTurnoActual.visibility = View.VISIBLE
        } else {
            holder.tvTurnoActual.visibility = View.GONE
        }

        // Personas en espera
        val personasTexto = if (turno.turnosEnEspera == 1) {
            "1 persona en espera"
        } else {
            "${turno.turnosEnEspera} personas en espera"
        }
        holder.tvPersonasEspera.text = personasTexto

        // Tiempo estimado
        holder.tvTiempoEstimado.text = "~${turno.tiempoEstimado} min de espera"

        // Click en la tarjeta
        holder.cardTurno.setOnClickListener {
            onTurnoClick(turno)
        }
    }

    override fun getItemCount() = turnos.size

    fun updateTurnos(newTurnos: List<ResumenTurno>) {
        turnos = newTurnos
        notifyDataSetChanged()
    }
}
