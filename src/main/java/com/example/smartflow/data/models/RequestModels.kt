package com.example.smartflow.data.models

// Request para login con credenciales
data class LoginRequest(
    val email: String,
    val password: String
)

// Request para login con Google
data class GoogleLoginRequest(
    val idToken: String,
    val rol: String? = null,
    val telefono: String? = null,
    val medicoInfo: MedicoInfoRequest? = null
)

// Request para registro
data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val password: String,
    val rol: String, // "paciente" o "medico"
    val medicoInfo: MedicoInfoRequest? = null
)

// Información del médico para registro
data class MedicoInfoRequest(
    val cedula: String,
    val especialidades: List<String>? = null,
    val tarifaConsulta: Double? = null,
    val descripcion: String? = null,
    val experiencia: String? = null
)

// Request para crear cita
data class CrearCitaRequest(
    val pacienteId: String,
    val medicoId: String,
    val fecha: String,      // Formato: "2025-11-20"
    val hora: String,       // Formato: "10:00"
    val motivo: String,
    val modoPago: String    // "online" o "efectivo"
)
