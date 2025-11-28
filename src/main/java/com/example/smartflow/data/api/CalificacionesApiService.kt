package com.example.smartflow.data.api

import com.example.smartflow.data.models.CalificacionRequest
import com.example.smartflow.data.models.CalificacionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CalificacionesApiService {

    @POST("calificaciones")
    fun calificarMedico(@Body request: CalificacionRequest): Call<CalificacionResponse>
}