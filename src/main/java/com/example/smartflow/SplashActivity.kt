package com.example.smartflow

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smartflow.data.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DURATION = 5000L // 5 segundos
    private val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Obtener vistas
        val ivLogo = findViewById<ImageView>(R.id.iv_logo)
        val tvAppName = findViewById<TextView>(R.id.tv_app_name)
        val tvTagline = findViewById<TextView>(R.id.tv_tagline)

        // Animación de fade in para el logo
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        fadeIn.duration = 1500
        ivLogo.startAnimation(fadeIn)
        tvAppName.startAnimation(fadeIn)
        tvTagline.startAnimation(fadeIn)

        // ✅ DESPERTAR EL SERVIDOR inmediatamente
        wakeUpServer()

        // Después de 5 segundos, ir a MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Cerrar splash para que no vuelva al presionar back
        }, SPLASH_DURATION)
    }

    /**
     * Despierta el servidor de Render haciendo una petición de "ping"
     * Esto evita que el usuario tenga que esperar 30-60 segundos cuando inicie sesión
     */
    private fun wakeUpServer() {
        Log.d(TAG, "🔥 Despertando servidor...")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Opción 1: Usar un endpoint simple que no requiera autenticación
                val response = RetrofitClient.especialidadesApiService.getEspecialidades().execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "✅ Servidor despierto y listo (${response.code()})")
                    } else {
                        Log.d(TAG, "⚠️ Servidor respondió pero con error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.w(TAG, "⚠️ No se pudo despertar el servidor: ${e.message}")
                    // No mostramos error al usuario porque no es crítico
                    // El usuario solo tendrá que esperar un poco más en el login
                }
            }
        }
    }
}