package com.example.smartflow.data.models

// Response al crear una cita
data class CrearCitaResponse(
    val success: Boolean,
    val message: String,
    val data: CitaCreada?
)

// Modelo de cita recién creada
data class CitaCreada(
    val _id: String,
    val fecha: String,
    val hora: String,
    val estado: String,
    val motivo: String,
    val monto: Int,
    val pagado: Boolean,
    val modoPago: String,
    val medico: MedicoCrearCita,
    val especialidad: EspecialidadSimple,  // ✅ NUEVO: especialidad guardada
    val paciente: PacienteCrearCita,
    val creadoEn: String
)

data class MedicoCrearCita(
    val _id: String,
    val nombre: String,
    val apellido: String,
    val foto: String?,
    val tarifaConsulta: Int
)

data class PacienteCrearCita(
    val _id: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val foto: String?
)

data class EspecialidadSimple(
    val _id: String,
    val nombre: String,
    val codigo: String,
    val descripcion: String?
)