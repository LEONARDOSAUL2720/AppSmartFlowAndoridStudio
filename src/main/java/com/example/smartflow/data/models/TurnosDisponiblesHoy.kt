package com.example.smartflow.data.models

// Respuesta de turnos disponibles HOY
data class TurnosDisponiblesHoyResponse(
    val success: Boolean,
    val fecha: String,
    val diaSemana: String,
    val data: List<MedicoConHorariosDisponibles>
)

data class MedicoConHorariosDisponibles(
    val medico: MedicoInfoTurno,
    val horarioTrabajo: HorarioTrabajo,
    val horariosDisponibles: List<String>,  // ["09:00", "09:30", "10:00"]
    val cantidadDisponibles: Int
)

data class MedicoInfoTurno(
    val _id: String,
    val nombre: String,
    val foto: String?,
    val especialidades: List<EspecialidadData>  // ✅ Reutilizamos EspecialidadData
)

data class HorarioTrabajo(
    val horaInicio: String,
    val horaFin: String
)