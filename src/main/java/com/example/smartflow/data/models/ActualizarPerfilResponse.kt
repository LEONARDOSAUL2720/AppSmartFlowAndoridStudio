package com.example.smartflow.data.models

data class ActualizarPerfilResponse(
    val success: Boolean,
    val message: String,
    val user: PerfilActualizadoData?  // ✅ Nombre único
)

data class PerfilActualizadoData(  // ✅ Nuevo nombre
    val id: String,
    val nombre: String,
    val apellido: String?,
    val email: String,
    val telefono: String,
    val rol: String,
    val foto: String?,
    val fechaRegistro: String?
)