package com.example.smartflow.data.api

import com.example.smartflow.data.models.*
import retrofit2.Call
import retrofit2.http.*

interface TurnosApiService {

    @GET("turnos/resumen/hoy")
    fun getResumenTurnosHoy(): Call<ResumenTurnosResponse>

    @GET("turnos/horarios-disponibles")
    fun getHorariosDisponibles(
        @Query("especialidadId") especialidadId: String? = null,
        @Query("medicoId") medicoId: String? = null
    ): Call<TurnosDisponiblesHoyResponse>  // ✅ CAMBIADO

    @GET("turnos/especialidad/{especialidadId}")
    fun getTurnosDelDia(
        @Path("especialidadId") especialidadId: String
    ): Call<TurnosResponse>

    @POST("turnos/tomar")
    fun tomarTurno(
        @Body body: TomarTurnoRequest
    ): Call<TurnoResponse>

    @GET("turnos/paciente/{pacienteId}/activo")
    fun getMiTurnoActivo(
        @Path("pacienteId") pacienteId: String
    ): Call<MiTurnoResponse>

    @DELETE("turnos/{turnoId}")
    fun cancelarTurno(
        @Path("turnoId") turnoId: String
    ): Call<TurnoResponse>
}

data class TomarTurnoRequest(
    val pacienteId: String,
    val especialidadId: String,
    val medicoId: String? = null,
    val motivo: String? = null
)

data class TurnoResponse(
    val success: Boolean,
    val data: Turno?,
    val message: String
)