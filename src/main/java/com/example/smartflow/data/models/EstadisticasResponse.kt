package com.example.smartflow.data.models

data class EstadisticasResponse(
    val success: Boolean,
    val data: EstadisticasPaciente
)

data class EstadisticasPaciente(
    val citasCompletadas: Int,
    val totalCitas: Int,
    val miembroDesde: Int,
    val estadoActivo: String
)