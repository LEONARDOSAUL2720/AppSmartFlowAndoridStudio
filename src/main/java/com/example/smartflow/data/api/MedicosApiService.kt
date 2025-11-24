package com.example.smartflow.data.api

import com.example.smartflow.data.models.MedicosResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MedicosApiService {
    
    @GET("medicos")
    fun getMedicos(): Call<MedicosResponse>
    
    @GET("medicos/especialidad/{especialidadId}")
    fun getMedicosPorEspecialidad(
        @Path("especialidadId") especialidadId: String
    ): Call<MedicosResponse>
}
