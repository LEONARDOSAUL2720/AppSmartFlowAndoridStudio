package com.example.smartflow.data.models

data class CambiosCitasResponse(
    val success: Boolean,
    val data: CambiosData?,
    val message: String?
)

data class CambiosData(
    val hayNuevas: Boolean,
    val ultimaActualizacion: Long
)