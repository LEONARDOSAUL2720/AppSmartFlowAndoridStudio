package com.example.smartflow.data.models

import com.google.gson.annotations.SerializedName


// Modelo del perfil del paciente
data class UsuarioPerfil(
    @SerializedName("_id") val _id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido") val apellido: String,
    @SerializedName("email") val email: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("foto") val foto: String? = null,
    @SerializedName("rol") val rol: String,
    @SerializedName("fechaRegistro") val fechaRegistro: String,
    @SerializedName("activo") val activo: Boolean
)
