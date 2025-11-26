package com.example.smartflow.data.api

import com.example.smartflow.data.models.RecetasResponse
import com.example.smartflow.data.models.RecetaDetalleResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecetasApiService {

    /**
     * Obtener todas las recetas de un paciente
     * GET /api/mobile/recetas/:pacienteId
     */
    @GET("recetas/{pacienteId}")
    fun getRecetasPaciente(
        @Path("pacienteId") pacienteId: String
    ): Call<RecetasResponse>

    /**
     * Obtener todas las recetas de un paciente filtradas por especialidad
     * GET /api/mobile/recetas/:pacienteId?especialidad=Cardiología
     */
    @GET("recetas/{pacienteId}")
    fun getRecetasPorEspecialidad(
        @Path("pacienteId") pacienteId: String,
        @Query("especialidad") especialidad: String
    ): Call<RecetasResponse>

    /**
     * Obtener detalle de una receta específica
     * GET /api/mobile/recetas/detalle/:recetaId
     */
    @GET("recetas/detalle/{recetaId}")
    fun getRecetaDetalle(
        @Path("recetaId") recetaId: String
    ): Call<RecetaDetalleResponse>
}