package com.example.smartflow.data.api

import com.example.smartflow.data.models.CambiosCitasResponse
import com.example.smartflow.data.models.CitasResponse
import com.example.smartflow.data.models.CitasHoyResponse
import com.example.smartflow.data.models.CrearCitaResponse
import com.example.smartflow.data.models.EstadisticasResponse
import com.example.smartflow.data.models.RequestCrearCita
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CitasApiService {
    
    @GET("citas/paciente/{pacienteId}/proximas")
    fun getCitasProximas(
        @Path("pacienteId") pacienteId: String
    ): Call<CitasResponse>
    
    @GET("citas/paciente/{pacienteId}/historial")
    fun getHistorialCitas(
        @Path("pacienteId") pacienteId: String
    ): Call<CitasResponse>
    
    @GET("citas/hoy")
    fun getCitasHoy(
        @Query("especialidadId") especialidadId: String? = null,
        @Query("medicoId") medicoId: String? = null
    ): Call<CitasHoyResponse>

    @POST("citas/crear")
    fun crearCita(
        @Body request: RequestCrearCita
    ): Call<CrearCitaResponse>

    @GET("citas/verificar-cambios/{userId}")
    fun verificarCambiosCitas(
        @Path("userId") userId: String,
        @Query("ultimaActualizacion") ultimaActualizacion: Long
    ): Call<CambiosCitasResponse>

    @GET("citas/paciente/{pacienteId}/estadisticas")
    fun getEstadisticasPaciente(
        @Path("pacienteId") pacienteId: String
    ): Call<EstadisticasResponse>
}
