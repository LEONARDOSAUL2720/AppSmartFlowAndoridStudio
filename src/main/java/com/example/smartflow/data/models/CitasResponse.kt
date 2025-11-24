package com.example.smartflow.data.models

data class CitasResponse(
    val success: Boolean,
    val count: Int,
    val data: List<Cita>,
    val message: String? = null
)