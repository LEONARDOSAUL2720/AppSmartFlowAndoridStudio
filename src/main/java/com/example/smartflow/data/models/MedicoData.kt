package com.example.smartflow.data.models

data class MedicosResponse(
    val success: Boolean,
    val data: List<MedicoData>
)

data class MedicoData(
    val _id: String,
    val nombre: String,
    val apellido: String,
    val foto: String?,
    val medicoInfo: MedicoInfoData
)

data class MedicoInfoData(
    val cedula: String?,
    val especialidades: List<EspecialidadData>,
    val tarifaConsulta: Int?,
    val calificacionPromedio: Double?,
    val totalCitasAtendidas: Int?
)
