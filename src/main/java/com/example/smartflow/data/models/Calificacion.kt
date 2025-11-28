package com.example.smartflow.data.models

import com.google.gson.annotations.SerializedName

data class CalificacionRequest(
    @SerializedName("medicoId") val medicoId: String,
    @SerializedName("pacienteId") val pacienteId: String,
    @SerializedName("calificacion") val calificacion: Int,
    @SerializedName("comentario") val comentario: String?
)

data class CalificacionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: CalificacionData?
)

data class CalificacionData(
    @SerializedName("medicoId") val medicoId: String,
    @SerializedName("nuevaCalificacionPromedio") val nuevaCalificacionPromedio: Double,
    @SerializedName("totalCalificaciones") val totalCalificaciones: Int
)