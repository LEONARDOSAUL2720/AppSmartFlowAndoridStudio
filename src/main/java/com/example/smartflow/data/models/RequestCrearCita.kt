// Archivo: RequestCrearCita.kt
package com.example.smartflow.data.models

data class RequestCrearCita(  // ⬅️ Nombre diferente
    val pacienteId: String,
    val medicoId: String,
    val especialidadId: String,
    val fecha: String,
    val hora: String,
    val motivo: String,
    val modoPago: String
)