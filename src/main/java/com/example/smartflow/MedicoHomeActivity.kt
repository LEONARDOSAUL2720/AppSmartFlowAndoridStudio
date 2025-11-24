package com.example.smartflow

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MedicoHomeActivity : AppCompatActivity() {

    private lateinit var googleClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medico_home)

        // Inicializar Firebase y Google Sign-In
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, gso)

        val tvWelcome = findViewById<TextView>(R.id.tv_welcome)
        val btnLogoutHeader = findViewById<ImageButton>(R.id.btn_logout_header)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Aplicar tint blanco al icono de logout para que contraste con el header verde
        btnLogoutHeader.setColorFilter(ContextCompat.getColor(this, R.color.white))

        // Aplicar tint verde a los iconos del bottom navigation
        val greenColor = ContextCompat.getColor(this, R.color.colorSecondaryVariant)
        bottomNavigation.itemIconTintList = ColorStateList.valueOf(greenColor)
        bottomNavigation.itemTextColor = ColorStateList.valueOf(greenColor)

        // Obtener datos del usuario desde SharedPreferences
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        val userNombre = prefs.getString("user_nombre", "Doctor")
        val userFoto = prefs.getString("user_foto", null)

        tvWelcome.text = "Bienvenido Dr(a). $userNombre"

        // Bottom Navigation Listener
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_pacientes -> {
                    Toast.makeText(this, "Mis Pacientes", Toast.LENGTH_SHORT).show()
                    // TODO: Navegar a lista de pacientes
                    true
                }
                R.id.nav_citas -> {
                    Toast.makeText(this, "Mis consultas", Toast.LENGTH_SHORT).show()
                    // TODO: Navegar a mis citas/consultas
                    true
                }
                R.id.nav_perfil -> {
                    Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
                    // TODO: Navegar a perfil profesional
                    true
                }
                else -> false
            }
        }

        // Seleccionar Home por defecto
        bottomNavigation.selectedItemId = R.id.nav_home

        // Botón de logout en el header
        btnLogoutHeader.setOnClickListener {
            // Cerrar sesión de Firebase
            auth.signOut()

            // Cerrar sesión de Google y limpiar caché
            googleClient.signOut().addOnCompleteListener(this) {
                // Limpiar SharedPreferences
                prefs.edit().clear().apply()

                // Limpiar caché de la app
                try {
                    val cache = cacheDir
                    cache.deleteRecursively()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Volver al login
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}
