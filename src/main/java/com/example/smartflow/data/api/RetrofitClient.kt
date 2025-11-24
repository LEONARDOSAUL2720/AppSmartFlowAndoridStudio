package com.example.smartflow.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Cambiar según ambiente
    private const val BASE_URL_EMULATOR = "http://10.0.2.2:3000/api/mobile/"
    private const val BASE_URL_DEVICE = "http://192.168.1.12:3000/api/mobile/"
    private const val BASE_URL_PRODUCTION = "https://smartflowmedic.onrender.com/api/mobile/"


    // Usar la URL apropiada
    private const val BASE_URL = BASE_URL_PRODUCTION
    //private const val BASE_URL = BASE_URL_DEVICE

    // Exponer la URL base sin el sufijo /api/mobile/ para uso directo
    //const val BACKEND_BASE_URL = "http://192.168.1.12:3000"
    const val BACKEND_BASE_URL = "https://smartflowmedic.onrender.com"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
    val citasApiService: CitasApiService = retrofit.create(CitasApiService::class.java)
    val turnosApiService: TurnosApiService = retrofit.create(TurnosApiService::class.java)
    val especialidadesApiService: EspecialidadesApiService = retrofit.create(EspecialidadesApiService::class.java)
    val medicosApiService: MedicosApiService = retrofit.create(MedicosApiService::class.java)
    val medicosDisponibilidadApiService: MedicosDisponibilidadApiService = retrofit.create(MedicosDisponibilidadApiService::class.java)
}
