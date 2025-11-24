package com.example.smartflow.utils

import android.graphics.Color

object EspecialidadColors {

    /**
     * Retorna un par de colores (inicio, fin) para el gradiente según el código de especialidad
     */
    fun getGradient(codigo: String): Pair<Int, Int> {
        return when (codigo.uppercase()) {
            "A" -> Pair(
                Color.parseColor("#E74C3C"), // Rojo claro
                Color.parseColor("#C0392B")  // Rojo oscuro - Cardiología
            )
            "B" -> Pair(
                Color.parseColor("#3498DB"), // Azul claro
                Color.parseColor("#2980B9")  // Azul oscuro - Dermatología
            )
            "C" -> Pair(
                Color.parseColor("#9B59B6"), // Púrpura claro
                Color.parseColor("#8E44AD")  // Púrpura oscuro - Pediatría
            )
            "M" -> Pair(
                Color.parseColor("#2A6FB0"), // Azul primario
                Color.parseColor("#1E5A8C")  // Azul oscuro - Medicina General
            )
            "E" -> Pair(
                Color.parseColor("#E67E22"), // Naranja claro
                Color.parseColor("#D35400")  // Naranja oscuro - Traumatología
            )
            "F" -> Pair(
                Color.parseColor("#1ABC9C"), // Turquesa claro
                Color.parseColor("#16A085")  // Turquesa oscuro - Neurología
            )
            "G" -> Pair(
                Color.parseColor("#F39C12"), // Amarillo claro
                Color.parseColor("#E67E22")  // Amarillo oscuro - Oftalmología
            )
            else -> Pair(
                Color.parseColor("#2A6FB0"), // Azul por defecto
                Color.parseColor("#1E5A8C")
            )
        }
    }

    /**
     * Retorna el color sólido principal de la especialidad
     */
    fun getColor(codigo: String): Int {
        return getGradient(codigo).first
    }

    /**
     * Retorna el nombre del color para debugging
     */
    fun getColorName(codigo: String): String {
        return when (codigo.uppercase()) {
            "A" -> "Rojo (Cardiología)"
            "B" -> "Azul (Dermatología)"
            "C" -> "Púrpura (Pediatría)"
            "M" -> "Azul Primario (Medicina General)"
            "E" -> "Naranja (Traumatología)"
            "F" -> "Turquesa (Neurología)"
            "G" -> "Amarillo (Oftalmología)"
            else -> "Azul Default"
        }
    }
}
