package com.example.smartflow

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.smartflow.adapters.CitasCardAdapter
import com.example.smartflow.adapters.TurnosDisponiblesAdapter
import com.example.smartflow.adapters.FilaVirtualAdapter
import com.example.smartflow.data.api.RetrofitClient
import com.example.smartflow.data.models.*
import android.widget.LinearLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PacienteHomeActivity : AppCompatActivity() {

    private lateinit var googleClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var citasAdapter: CitasCardAdapter
    private lateinit var turnosDisponiblesAdapter: TurnosDisponiblesAdapter  // ✅ CAMBIADO
    private lateinit var filaVirtualAdapter: FilaVirtualAdapter
    private lateinit var rvCitas: RecyclerView
    private lateinit var rvTurnos: RecyclerView
    private lateinit var rvFilaVirtual: RecyclerView
    private lateinit var tvNoCitas: TextView
    private lateinit var tvNoTurnos: TextView
    private lateinit var tvNoFila: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    // Contenedores de filtros
    private lateinit var llFiltrosCitas: LinearLayout
    private lateinit var llFiltrosTurnos: LinearLayout
    private lateinit var llFiltrosFila: LinearLayout

    // Lista de especialidades desde BD
    private var especialidades: List<EspecialidadData> = emptyList()

    // ✅ POLLING: Variables para actualización inteligente
    private val pollingHandler = Handler(Looper.getMainLooper())
    private var ultimaActualizacion = System.currentTimeMillis()
    private val POLLING_INTERVAL = 15000L // 15 segundos
    private var isPollingActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_home)

        // Inicializar Firebase y Google Sign-In
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, gso)

        val tvWelcome = findViewById<TextView>(R.id.tv_welcome)
        val ivUserPhoto = findViewById<ImageView>(R.id.iv_user_photo)
        val btnNotifications = findViewById<ImageButton>(R.id.btn_notifications)
        val btnLogoutHeader = findViewById<ImageButton>(R.id.btn_logout_header)
        bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        rvCitas = findViewById(R.id.rv_citas)
        rvTurnos = findViewById(R.id.rv_turnos)
        rvFilaVirtual = findViewById(R.id.rv_fila_virtual)
        tvNoCitas = findViewById(R.id.tv_no_citas)
        tvNoTurnos = findViewById(R.id.tv_no_turnos)
        tvNoFila = findViewById(R.id.tv_no_fila)

        // Inicializar contenedores de filtros
        llFiltrosCitas = findViewById(R.id.ll_filtros_citas)
        llFiltrosTurnos = findViewById(R.id.ll_filtros_turnos)
        llFiltrosFila = findViewById(R.id.ll_filtros_fila)

        // Configurar RecyclerView de Citas (horizontal)
        citasAdapter = CitasCardAdapter(mutableListOf()) { cita ->
            Toast.makeText(this, "Cita con ${cita.medico.nombre}", Toast.LENGTH_SHORT).show()
        }
        rvCitas.apply {
            layoutManager = LinearLayoutManager(this@PacienteHomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = citasAdapter
        }

        // ✅ NUEVO: Configurar RecyclerView de Turnos Disponibles HOY
        turnosDisponiblesAdapter = TurnosDisponiblesAdapter(emptyList()) { medicoId, medicoNombre, especialidad, hora ->
            Toast.makeText(
                this,
                "Turno seleccionado:\n$hora\nMédico: $medicoNombre\nEspecialidad: $especialidad",
                Toast.LENGTH_LONG
            ).show()
            // TODO: Implementar lógica para tomar el turno
        }
        rvTurnos.apply {
            layoutManager = LinearLayoutManager(this@PacienteHomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = turnosDisponiblesAdapter
        }

        // Configurar RecyclerView de Fila Virtual (horizontal)
        filaVirtualAdapter = FilaVirtualAdapter(emptyList())
        rvFilaVirtual.apply {
            layoutManager = LinearLayoutManager(this@PacienteHomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = filaVirtualAdapter
        }

        // Deshabilitar tint en los iconos
        bottomNavigation.itemIconTintList = null

        // Obtener datos del usuario
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userNombre = prefs.getString("user_nombre", "Paciente")
        val userId = prefs.getString("user_id", null)
        val userFoto = prefs.getString("user_foto", null)

        tvWelcome.text = userNombre ?: "Paciente"

        // Cargar foto de perfil
        if (!userFoto.isNullOrEmpty()) {
            ivUserPhoto.load(userFoto) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                error(R.drawable.ic_launcher_foreground)
                transformations(CircleCropTransformation())
            }
        }

        // Click en notificaciones
        btnNotifications.setOnClickListener {
            Toast.makeText(this, "Notificaciones próximamente", Toast.LENGTH_SHORT).show()
        }

        // Cargar especialidades
        cargarEspecialidades()

        // Cargar datos iniciales
        if (userId != null) {
            cargarCitasProximas(userId)
            cargarTurnosDisponiblesHoy()  // ✅ CAMBIADO
            cargarFilaVirtualHoy()
        } else {
            Toast.makeText(this, "Error: No se encontró el ID del usuario", Toast.LENGTH_SHORT).show()
        }

        // Bottom Navigation
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_recetas -> {
                    val intent = Intent(this, RecetasHistorialActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_citas -> {
                    val intent = Intent(this, GenerarCitaActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_perfil -> {
                    val intent = Intent(this, PacientePerfilActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        bottomNavigation.selectedItemId = R.id.nav_home

        // Logout
        btnLogoutHeader.setOnClickListener {
            auth.signOut()
            googleClient.signOut().addOnCompleteListener(this) {
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

        // Verificar si viene de crear cita
        if (intent.getBooleanExtra("refrescar_citas", false)) {
            if (userId != null) {
                cargarCitasProximas(userId)
            }
        }
    }

    // ✅ POLLING: Iniciar actualización inteligente
    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userId = prefs.getString("user_id", null)

        if (userId != null) {
            cargarCitasProximas(userId)

            // ✅ Iniciar polling si no está activo
            if (!isPollingActive) {
                iniciarPollingInteligente()
            }
        }
    }

    // ✅ POLLING: Detener cuando la app no está visible
    override fun onPause() {
        super.onPause()
        detenerPolling()
    }

    override fun onDestroy() {
        super.onDestroy()
        detenerPolling()
    }

    // ✅ POLLING: Iniciar verificación periódica
    private fun iniciarPollingInteligente() {
        isPollingActive = true
        Log.d("Polling", "🔄 Polling iniciado (cada ${POLLING_INTERVAL/1000}s)")

        pollingHandler.postDelayed(object : Runnable {
            override fun run() {
                if (isPollingActive) {
                    verificarCambiosCitas()
                    pollingHandler.postDelayed(this, POLLING_INTERVAL)
                }
            }
        }, POLLING_INTERVAL)
    }

    // ✅ POLLING: Detener verificación
    private fun detenerPolling() {
        isPollingActive = false
        pollingHandler.removeCallbacksAndMessages(null)
        Log.d("Polling", "⏸️ Polling detenido")
    }

    // ✅ POLLING: Verificar si hay cambios en las citas
    private fun verificarCambiosCitas() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userId = prefs.getString("user_id", null) ?: return

        val citasService = RetrofitClient.citasApiService

        citasService.verificarCambiosCitas(userId, ultimaActualizacion).enqueue(object : Callback<CambiosCitasResponse> {
            override fun onResponse(call: Call<CambiosCitasResponse>, response: Response<CambiosCitasResponse>) {
                if (response.isSuccessful) {
                    val cambios = response.body()
                    if (cambios?.success == true && cambios.data?.hayNuevas == true) {
                        Log.d("Polling", "✅ Hay cambios nuevos, recargando citas...")
                        cargarCitasProximasSilencioso(userId)
                        ultimaActualizacion = cambios.data.ultimaActualizacion
                    } else {
                        Log.d("Polling", "ℹ️ No hay cambios")
                    }
                } else {
                    Log.e("Polling", "❌ Error verificando cambios: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CambiosCitasResponse>, t: Throwable) {
                Log.e("Polling", "❌ Error de red: ${t.message}")
            }
        })
    }

    // ✅ POLLING: Cargar citas sin mostrar loading (silencioso)
    private fun cargarCitasProximasSilencioso(userId: String) {
        val citasService = RetrofitClient.citasApiService

        citasService.getCitasProximas(userId).enqueue(object : Callback<CitasResponse> {
            override fun onResponse(call: Call<CitasResponse>, response: Response<CitasResponse>) {
                if (response.isSuccessful) {
                    val citasResponse = response.body()
                    if (citasResponse?.success == true) {
                        val citasNuevas = citasResponse.data
                        actualizarCitasInteligente(citasNuevas)
                    }
                }
            }

            override fun onFailure(call: Call<CitasResponse>, t: Throwable) {
                Log.e("Polling", "Error cargando citas: ${t.message}")
            }
        })
    }

    // ✅ POLLING: Actualizar solo lo que cambió
    private fun actualizarCitasInteligente(citasNuevas: List<Cita>) {
        val citasViejas = todasLasCitas
        val citasViejasIds = citasViejas.map { it._id }.toSet()
        val citasNuevasIds = citasNuevas.map { it._id }.toSet()

        // Detectar citas nuevas
        citasNuevas.forEach { citaNueva ->
            if (!citasViejasIds.contains(citaNueva._id)) {
                Log.d("Polling", "➕ Nueva cita detectada: ${citaNueva._id}")
                todasLasCitas = (listOf(citaNueva) + todasLasCitas)
                citasAdapter.agregarCita(citaNueva)
            }
        }

        // Detectar citas eliminadas
        citasViejas.forEach { citaVieja ->
            if (!citasNuevasIds.contains(citaVieja._id)) {
                Log.d("Polling", "➖ Cita eliminada: ${citaVieja._id}")
                todasLasCitas = todasLasCitas.filter { it._id != citaVieja._id }
                citasAdapter.eliminarCitaPorId(citaVieja._id)
            }
        }

        // Actualizar visibilidad
        if (todasLasCitas.isNotEmpty()) {
            rvCitas.visibility = View.VISIBLE
            tvNoCitas.visibility = View.GONE
        } else {
            rvCitas.visibility = View.GONE
            tvNoCitas.visibility = View.VISIBLE
        }
    }

    private fun cargarEspecialidades() {
        val especialidadesService = RetrofitClient.especialidadesApiService

        especialidadesService.getEspecialidades().enqueue(object : Callback<EspecialidadResponse> {
            override fun onResponse(call: Call<EspecialidadResponse>, response: Response<EspecialidadResponse>) {
                if (response.isSuccessful) {
                    val especialidadesResponse = response.body()
                    if (especialidadesResponse?.success == true) {
                        especialidades = especialidadesResponse.data
                        generarChipsFiltros()
                    }
                }
            }

            override fun onFailure(call: Call<EspecialidadResponse>, t: Throwable) {
                Log.e("PacienteHome", "Error de red especialidades: ${t.message}", t)
            }
        })
    }

    private fun generarChipsFiltros() {
        generarChipsParaSeccion(llFiltrosCitas, "citas")
        generarChipsParaSeccion(llFiltrosTurnos, "turnos")
        generarChipsParaSeccion(llFiltrosFila, "fila")
    }

    private fun generarChipsParaSeccion(container: LinearLayout, seccion: String) {
        container.removeAllViews()

        val chipTodas = TextView(this).apply {
            text = "Todas"
            textSize = 14f
            setTextColor(resources.getColorStateList(R.color.chip_text_color, null))
            background = resources.getDrawable(R.drawable.chip_selector, null)
            minHeight = resources.getDimensionPixelSize(R.dimen.chip_min_height)
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(32, 12, 32, 12)
            isClickable = true
            isFocusable = true
            isSelected = true

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = 24
            layoutParams = params

            setOnClickListener {
                seleccionarChip(container, this, null, seccion)
            }
        }
        container.addView(chipTodas)

        especialidades.forEach { especialidad ->
            val chip = TextView(this).apply {
                text = especialidad.nombre
                tag = especialidad._id
                textSize = 14f
                setTextColor(resources.getColorStateList(R.color.chip_text_color, null))
                background = resources.getDrawable(R.drawable.chip_selector, null)
                minHeight = resources.getDimensionPixelSize(R.dimen.chip_min_height)
                gravity = android.view.Gravity.CENTER_VERTICAL
                setPadding(32, 12, 32, 12)
                isClickable = true
                isFocusable = true

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.marginEnd = 24
                layoutParams = params

                setOnClickListener {
                    seleccionarChip(container, this, especialidad.nombre, seccion)
                }
            }
            container.addView(chip)
        }
    }

    private fun seleccionarChip(container: LinearLayout, chipSeleccionado: TextView, filtro: String?, seccion: String) {
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            if (child is TextView) {
                child.isSelected = false
            }
        }

        chipSeleccionado.isSelected = true

        when (seccion) {
            "citas" -> filtrarCitas(filtro)
            "turnos" -> filtrarTurnosDisponibles(filtro)  // ✅ CAMBIADO
            "fila" -> filtrarFilaVirtual(filtro)
        }
    }

    private fun cargarCitasProximas(userId: String) {
        val citasService = RetrofitClient.citasApiService

        citasService.getCitasProximas(userId).enqueue(object : Callback<CitasResponse> {
            override fun onResponse(call: Call<CitasResponse>, response: Response<CitasResponse>) {
                if (response.isSuccessful) {
                    val citasResponse = response.body()
                    if (citasResponse?.success == true) {
                        val citas = citasResponse.data
                        todasLasCitas = citas
                        if (citas.isNotEmpty()) {
                            citasAdapter.updateCitas(citas)
                            rvCitas.visibility = View.VISIBLE
                            tvNoCitas.visibility = View.GONE
                        } else {
                            rvCitas.visibility = View.GONE
                            tvNoCitas.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CitasResponse>, t: Throwable) {
                Log.e("PacienteHome", "Error de red: ${t.message}", t)
                rvCitas.visibility = View.GONE
                tvNoCitas.visibility = View.VISIBLE
            }
        })
    }

    private var todasLasCitas: List<Cita> = emptyList()
    private var todosLosTurnosDisponibles: List<MedicoConHorariosDisponibles> = emptyList()  // ✅ CAMBIADO
    private var todasLasCitasHoy: List<CitaHoy> = emptyList()

    private fun filtrarCitas(especialidad: String?) {
        val citasFiltradas = if (especialidad == null) {
            todasLasCitas
        } else {
            todasLasCitas.filter { it.medico.especialidad == especialidad }
        }
        citasAdapter.updateCitas(citasFiltradas)
    }

    // ✅ NUEVO: Filtrar turnos disponibles por especialidad
    private fun filtrarTurnosDisponibles(especialidad: String?) {
        val turnosFiltrados = if (especialidad == null) {
            todosLosTurnosDisponibles
        } else {
            todosLosTurnosDisponibles.filter { turno ->
                turno.medico.especialidades.any { it.nombre == especialidad }
            }
        }
        turnosDisponiblesAdapter.updateTurnos(turnosFiltrados)
    }

    // ✅ NUEVO: Cargar turnos disponibles HOY
    private fun cargarTurnosDisponiblesHoy(especialidadId: String? = null) {
        val turnosService = RetrofitClient.turnosApiService

        Log.d("TurnosHoy", "🔄 Cargando turnos disponibles hoy...")

        turnosService.getHorariosDisponibles(especialidadId, null).enqueue(object : Callback<TurnosDisponiblesHoyResponse> {
            override fun onResponse(
                call: Call<TurnosDisponiblesHoyResponse>,
                response: Response<TurnosDisponiblesHoyResponse>
            ) {
                if (response.isSuccessful) {
                    val turnosResponse = response.body()
                    Log.d("TurnosHoy", "✅ Respuesta: ${turnosResponse?.data?.size} médicos disponibles")

                    if (turnosResponse?.success == true) {
                        val turnos = turnosResponse.data
                        todosLosTurnosDisponibles = turnos

                        if (turnos.isNotEmpty()) {
                            turnosDisponiblesAdapter.updateTurnos(turnos)
                            rvTurnos.visibility = View.VISIBLE
                            tvNoTurnos.visibility = View.GONE

                            val totalTurnos = turnos.sumOf { it.cantidadDisponibles }
                            Log.d("TurnosHoy", "✅ $totalTurnos turnos disponibles en ${turnos.size} médicos")
                        } else {
                            Log.d("TurnosHoy", "ℹ️ No hay turnos disponibles hoy (${turnosResponse.diaSemana})")
                            rvTurnos.visibility = View.GONE
                            tvNoTurnos.text = "No hay médicos disponibles hoy"
                            tvNoTurnos.visibility = View.VISIBLE
                        }
                    }
                } else {
                    Log.e("TurnosHoy", "❌ Error HTTP: ${response.code()}")
                    rvTurnos.visibility = View.GONE
                    tvNoTurnos.text = "Error al cargar turnos"
                    tvNoTurnos.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<TurnosDisponiblesHoyResponse>, t: Throwable) {
                Log.e("TurnosHoy", "❌ Error de red: ${t.message}")
                rvTurnos.visibility = View.GONE
                tvNoTurnos.text = "Sin conexión"
                tvNoTurnos.visibility = View.VISIBLE
            }
        })
    }

    private fun cargarFilaVirtualHoy(especialidadId: String? = null, medicoId: String? = null) {
        val citasService = RetrofitClient.citasApiService

        citasService.getCitasHoy(especialidadId, medicoId).enqueue(object : Callback<CitasHoyResponse> {
            override fun onResponse(call: Call<CitasHoyResponse>, response: Response<CitasHoyResponse>) {
                if (response.isSuccessful) {
                    val citasResponse = response.body()
                    if (citasResponse?.success == true) {
                        val citasAplanadas = citasResponse.data.flatMap { it.citas }
                        todasLasCitasHoy = citasAplanadas

                        if (citasAplanadas.isNotEmpty()) {
                            filaVirtualAdapter.updateCitas(citasAplanadas)
                            rvFilaVirtual.visibility = View.VISIBLE
                            tvNoFila.visibility = View.GONE
                        } else {
                            rvFilaVirtual.visibility = View.GONE
                            tvNoFila.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CitasHoyResponse>, t: Throwable) {
                Log.e("PacienteHome", "Error de red fila virtual: ${t.message}", t)
                rvFilaVirtual.visibility = View.GONE
                tvNoFila.visibility = View.VISIBLE
            }
        })
    }

    private fun filtrarFilaVirtual(especialidad: String?) {
        val citasFiltradas = if (especialidad == null) {
            todasLasCitasHoy
        } else {
            todasLasCitasHoy.filter { cita ->
                cita.medico.especialidad == especialidad
            }
        }

        if (citasFiltradas.isNotEmpty()) {
            filaVirtualAdapter.updateCitas(citasFiltradas)
            rvFilaVirtual.visibility = View.VISIBLE
            tvNoFila.visibility = View.GONE
        } else {
            rvFilaVirtual.visibility = View.GONE
            tvNoFila.visibility = View.VISIBLE
        }
    }
}