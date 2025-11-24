package com.example.smartflow.data.models

data class EspecialidadResponse(
    val success: Boolean,
    val data: List<EspecialidadData>
)

data class EspecialidadData(
    val _id: String,
    val nombre: String,
    val codigo: String,
    val descripcion: String
)
