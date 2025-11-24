package com.example.smartflow.data.models

data class HorariosDisponiblesResponse(
    val success: Boolean,
    val fecha: String,
    val data: List<MedicoHorarios>
)

data class MedicoHorarios(
    val medico: MedicoHorario,
    val horarios: List<HorarioSlot>
)

data class MedicoHorario(
    val _id: String,
    val nombre: String,
    val foto: String?,
    val especialidades: List<EspecialidadData>
)

data class HorarioSlot(
    val horaInicio: String,
    val horaFin: String,
    val turnosEnEspera: Int,
    val capacidadMaxima: Int,
    val disponible: Boolean,
    val tiempoEstimadoMin: Int
)
