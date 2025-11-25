package com.example.smartflow

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class PacientePerfilActivity : AppCompatActivity() {

    private lateinit var ivUserPhoto: ImageView
    private lateinit var tvUserNombre: TextView
    private lateinit var tvUserRol: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserTelefono: TextView
    private lateinit var tvTotalCitas: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var btnShare: ImageButton
    private lateinit var btnEditarPerfil: MaterialButton
    private lateinit var btnVerCitas: MaterialButton
    private lateinit var btnCerrarSesion: MaterialButton

    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_perfil)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, gso)

        // Inicializar vistas
        ivUserPhoto = findViewById(R.id.iv_user_photo)
        tvUserNombre = findViewById(R.id.tv_user_nombre)
        tvUserRol = findViewById(R.id.tv_user_rol)
        tvUserEmail = findViewById(R.id.tv_user_email)
        tvUserTelefono = findViewById(R.id.tv_user_telefono)
        tvTotalCitas = findViewById(R.id.tv_total_citas)
        btnBack = findViewById(R.id.btn_back)
        btnShare = findViewById(R.id.btn_share)
        btnEditarPerfil = findViewById(R.id.btn_editar_perfil)
        btnVerCitas = findViewById(R.id.btn_ver_citas)
        btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion)

        // Botón de regresar
        btnBack.setOnClickListener {
            finish()
        }

        // Botón compartir
        btnShare.setOnClickListener {
            compartirPerfil()
        }

        // Cargar datos del usuario logueado
        cargarDatosUsuario()

        // Ver citas
        btnVerCitas.setOnClickListener {
            Toast.makeText(this, "Ver citas próximamente", Toast.LENGTH_SHORT).show()
        }

        // Editar perfil
        btnEditarPerfil.setOnClickListener {
            Toast.makeText(this, "Editar perfil próximamente", Toast.LENGTH_SHORT).show()
        }

        // Cerrar sesión
        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cargarDatosUsuario() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

        val nombre = prefs.getString("user_nombre", "Usuario") ?: "Usuario"
        val email = prefs.getString("user_email", "email@ejemplo.com") ?: "email@ejemplo.com"
        val telefono = prefs.getString("user_telefono", "Sin teléfono") ?: "Sin teléfono"
        val foto = prefs.getString("user_foto", null)
        val rol = prefs.getString("user_rol", "paciente") ?: "paciente"

        // Mostrar datos
        tvUserNombre.text = nombre
        tvUserRol.text = rol.replaceFirstChar { it.uppercase() }
        tvUserEmail.text = email
        tvUserTelefono.text = telefono

        // TODO: Cargar número real de citas desde el backend
        tvTotalCitas.text = "0 citas agendadas"

        // Cargar foto
        if (!foto.isNullOrEmpty()) {
            ivUserPhoto.load(foto) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                error(R.drawable.ic_launcher_foreground)
                transformations(CircleCropTransformation())
            }
        }
    }
    private fun compartirPerfil() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val nombre = prefs.getString("user_nombre", "Usuario") ?: "Usuario"
        val email = prefs.getString("user_email", "") ?: ""

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Perfil de $nombre\n" +
                        "Email: $email\n" +
                        "App: SmartFlow"
            )
        }
        startActivity(Intent.createChooser(shareIntent, "Compartir perfil"))
    }

    private fun cerrarSesion() {
        auth.signOut()
        googleClient.signOut().addOnCompleteListener(this) {
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            prefs.edit().clear().apply()

            try {
                cacheDir.deleteRecursively()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}