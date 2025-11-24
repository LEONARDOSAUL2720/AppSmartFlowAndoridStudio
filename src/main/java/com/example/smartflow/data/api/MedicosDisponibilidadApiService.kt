package com.example.smartflow.data.api

import com.example.smartflow.data.models.DisponibilidadDia
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MedicosDisponibilidadApiService {
    @GET("medicos/{id}/disponibilidad")
    fun getDisponibilidad(@Path("id") medicoId: String): Call<com.example.smartflow.data.models.DisponibilidadResponse>
}
