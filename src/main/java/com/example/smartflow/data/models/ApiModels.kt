package com.example.smartflow.data.models

// Respuesta genérica del backend
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null,
    
    // Campos específicos de Google Login (primer registro)
    val requiresUserType: Boolean? = null,
    val requiresPhone: Boolean? = null
)

// Respuesta de Login/Register
data class LoginResponse(
    val success: Boolean,
    val token: String,
    val user: UserData,
    val message: String? = null
)

// Datos del usuario
data class UserData(
    val id: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String? = null,
    val rol: String, // "paciente" o "medico"
    val foto: String? = null,
    val platform: String? = null,
    val activo: Boolean? = null,
    val fechaRegistro: String? = null,
    val medicoInfo: MedicoInfo? = null
)

// Información específica del médico
data class MedicoInfo(
    val cedula: String? = null,
    val especialidades: List<String>? = null,
    val tarifaConsulta: Double? = null,
    val descripcion: String? = null,
    val experiencia: String? = null,
    val calificacionPromedio: Double? = null,
    val totalCitasAtendidas: Int? = null,
    val ubicacion: Ubicacion? = null,
    val horariosDisponibles: List<Horario>? = null
)

// Ubicación del médico
data class Ubicacion(
    val calle: String? = null,
    val ciudad: String? = null,
    val estado: String? = null,
    val codigoPostal: String? = null,
    val coordenadas: Coordenadas? = null
)

data class Coordenadas(
    val latitud: Double? = null,
    val longitud: Double? = null
)

// Horarios disponibles
data class Horario(
    val dia: String, // "lunes", "martes", etc.
    val horaInicio: String, // "09:00"
    val horaFin: String // "17:00"
)
