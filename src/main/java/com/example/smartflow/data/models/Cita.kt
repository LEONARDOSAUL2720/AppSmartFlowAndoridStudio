package com.example.smartflow.data.models

data class Cita(
    val _id: String,
    val fecha: String,
    val hora: String,
    val estado: String,
    val motivo: String,
    val monto: Int,
    val medico: Medico
)
