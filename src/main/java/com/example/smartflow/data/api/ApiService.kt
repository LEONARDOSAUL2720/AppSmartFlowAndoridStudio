package com.example.smartflow.data.api

import com.example.smartflow.data.models.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // ============= AUTENTICACIÓN =============
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>
    
    @POST("auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<LoginResponse>
    
    @GET("auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ApiResponse<UserData>>
    
    @PUT("auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body userData: Map<String, Any>
    ): Response<ApiResponse<UserData>>
    
    // ============= CITAS =============
    
    @GET("citas")
    suspend fun getCitas(@Header("Authorization") token: String): Response<ApiResponse<List<Any>>>
    
    @POST("citas")
    suspend fun createCita(
        @Header("Authorization") token: String,
        @Body citaData: Map<String, Any>
    ): Response<ApiResponse<Any>>
    
    // ============= MÉDICOS =============
    
    @GET("medicos")
    suspend fun getMedicos(): Response<ApiResponse<List<UserData>>>
    
    @GET("medicos/{id}")
    suspend fun getMedicoById(@Path("id") medicoId: String): Response<ApiResponse<UserData>>
    
    // ============= ESPECIALIDADES =============
    
    @GET("especialidades")
    suspend fun getEspecialidades(): Response<ApiResponse<List<Any>>>


    @PUT("auth/profile")
    fun actualizarPerfil(
        @Header("Authorization") token: String,
        @Body datos: ActualizarPerfilRequest
    ): Call<ActualizarPerfilResponse>
}
