package com.example.smartflow.data.models

data class ActualizarPerfilRequest(
    val nombre: String,
    val apellido: String?,
    val email: String,
    val telefono: String,
    val password: String? = null,
    val foto: String? = null
)