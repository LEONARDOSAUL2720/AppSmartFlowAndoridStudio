package com.example.smartflow.data.api

import com.example.smartflow.data.models.*
import retrofit2.Call
import retrofit2.http.*

interface MedicosApiService {

    // Obtener todos los médicos
    @GET("medicos")
    fun getMedicos(): Call<MedicosResponse>

    // Buscar médicos por especialidad
    @GET("medicos/especialidad/{especialidadId}")
    fun getMedicosPorEspecialidad(
        @Path("especialidadId") especialidadId: String
    ): Call<MedicosResponse>
}