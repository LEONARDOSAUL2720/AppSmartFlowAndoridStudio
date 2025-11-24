package com.example.smartflow.data.api

import com.example.smartflow.data.models.EspecialidadResponse
import retrofit2.Call
import retrofit2.http.GET

interface EspecialidadesApiService {
    
    @GET("especialidades")
    fun getEspecialidades(): Call<EspecialidadResponse>
}
