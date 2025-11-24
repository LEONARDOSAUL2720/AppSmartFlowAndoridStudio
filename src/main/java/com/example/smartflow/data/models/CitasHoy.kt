package com.example.smartflow.data.models

data class CitasHoyResponse(
    val success: Boolean,
    val fecha: String,
    val totalCitas: Int,
    val data: List<CitasPorHora>
)

data class CitasPorHora(
    val hora: String,
    val citas: List<CitaHoy>
)

data class CitaHoy(
    val _id: String,
    val hora: String,
    val estado: String,
    val motivo: String,
    val medico: MedicoCitaHoy,
    val paciente: PacienteCitaHoy
)

data class MedicoCitaHoy(
    val _id: String,
    val nombre: String,
    val foto: String?,
    val especialidad: String,
    val especialidadCodigo: String
)

data class PacienteCitaHoy(
    val _id: String,
    val nombre: String,
    val foto: String?
)
