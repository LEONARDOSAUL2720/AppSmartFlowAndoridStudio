package com.example.smartflow.data.models

import com.google.gson.annotations.SerializedName

// Modelo principal de Receta
data class Receta(
    @SerializedName("_id") val id: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("medicamentos") val medicamentos: List<Medicamento>,
    @SerializedName("observaciones") val observaciones: String?,
    @SerializedName("pdfUrl") val pdfUrl: String?,
    @SerializedName("qrCode") val qrCode: String?,
    @SerializedName("medico") val medico: MedicoReceta,
    @SerializedName("cita") val cita: CitaReceta
)

// Sub-modelo: Medicamento
data class Medicamento(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("dosis") val dosis: String,
    @SerializedName("frecuencia") val frecuencia: String,
    @SerializedName("duracion") val duracion: String
)

// Sub-modelo: Información del médico en la receta
data class MedicoReceta(
    @SerializedName("_id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("foto") val foto: String?,
    @SerializedName("especialidad") val especialidad: String,
    @SerializedName("cedulaProfesional") val cedulaProfesional: String?
)

// Sub-modelo: Información de la cita asociada
data class CitaReceta(
    @SerializedName("_id") val id: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("hora") val hora: String,
    @SerializedName("estado") val estado: String
)

// Respuesta del endpoint GET /api/mobile/recetas/:pacienteId
data class RecetasResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("count") val count: Int,
    @SerializedName("data") val data: List<Receta>
)

// Respuesta del endpoint GET /api/mobile/recetas/detalle/:recetaId
data class RecetaDetalleResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: Receta
)