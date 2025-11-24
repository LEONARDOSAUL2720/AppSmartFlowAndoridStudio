package com.example.smartflow.data.models

data class Turno(
    val _id: String,
    val paciente: String,
    val medico: Medico,
    val especialidad: Especialidad,
    val fecha: String,
    val turno: String,              // "A-01", "B-03"
    val posicionEnFila: Int,
    val tiempoEstimadoMin: Int,
    val estado: String,             // "en_espera", "llamando", "atendiendo", "completado"
    val esTurnoVirtual: Boolean,
    val motivo: String,
    val horaLlegada: String
)

data class Especialidad(
    val _id: String,
    val nombre: String,
    val codigo: String
)

data class ResumenTurno(
    val especialidad: Especialidad,
    val turnosEnEspera: Int,
    val turnoActual: String?,
    val tiempoEstimado: Int,
    val disponible: Boolean
)

data class TurnosResponse(
    val success: Boolean,
    val count: Int,
    val data: List<Turno>,
    val message: String? = null
)

data class ResumenTurnosResponse(
    val success: Boolean,
    val count: Int,
    val data: List<ResumenTurno>
)

data class MiTurnoResponse(
    val success: Boolean,
    val data: MiTurnoData?,
    val message: String? = null
)

data class MiTurnoData(
    val _id: String,
    val turno: String,
    val estado: String,
    val posicionEnFila: Int,
    val tiempoEstimadoMin: Int,
    val especialidad: Especialidad,
    val medico: Medico,
    val motivo: String,
    val horaLlegada: String,
    val turnosAdelante: Int
)
