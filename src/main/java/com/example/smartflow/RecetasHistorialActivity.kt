package com.example.smartflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartflow.adapters.RecetasAdapter
import com.example.smartflow.data.api.RetrofitClient
import com.example.smartflow.data.models.Receta
import com.example.smartflow.data.models.RecetasResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecetasHistorialActivity : AppCompatActivity() {

    private lateinit var rvRecetas: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var recetasAdapter: RecetasAdapter
    private lateinit var llFiltrosRecetas: LinearLayout
    private lateinit var btnBack: ImageButton
    private lateinit var bottomNavigation: BottomNavigationView

    // Lista completa de recetas (sin filtrar)
    private var todasLasRecetas: List<Receta> = emptyList()

    // Lista de especialidades para filtros
    private var especialidades: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recetas_historial)

        // Inicializar vistas
        rvRecetas = findViewById(R.id.rv_recetas)
        emptyState = findViewById(R.id.empty_state)
        llFiltrosRecetas = findViewById(R.id.ll_filtros_recetas)
        btnBack = findViewById(R.id.btn_back)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Configurar RecyclerView
        recetasAdapter = RecetasAdapter(emptyList()) { receta ->
            // Click en "Ver Detalles" - Mostrar diálogo (próximo paso)
            mostrarDetallesReceta(receta)
        }

        rvRecetas.apply {
            layoutManager = LinearLayoutManager(this@RecetasHistorialActivity)
            adapter = recetasAdapter
        }

        // Botón de regreso
        btnBack.setOnClickListener {
            finish()
        }

        // Bottom Navigation
        bottomNavigation.itemIconTintList = null
        bottomNavigation.selectedItemId = R.id.nav_recetas

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, PacienteHomeActivity::class.java)
                    startActivity(intent)
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

        // Cargar recetas del paciente
        cargarRecetas()
    }

    private fun cargarRecetas() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userId = prefs.getString("user_id", null)

        if (userId == null) {
            Toast.makeText(this, "Error: No se encontró el ID del usuario", Toast.LENGTH_SHORT).show()
            mostrarEstadoVacio()
            return
        }

        val recetasService = RetrofitClient.recetasApiService

        Log.d("RecetasHistorial", "🔄 Cargando recetas del paciente: $userId")

        recetasService.getRecetasPaciente(userId).enqueue(object : Callback<RecetasResponse> {
            override fun onResponse(call: Call<RecetasResponse>, response: Response<RecetasResponse>) {
                if (response.isSuccessful) {
                    val recetasResponse = response.body()
                    Log.d("RecetasHistorial", "✅ Respuesta exitosa: ${recetasResponse?.count} recetas")

                    if (recetasResponse?.success == true) {
                        todasLasRecetas = recetasResponse.data

                        if (todasLasRecetas.isNotEmpty()) {
                            // Extraer especialidades únicas para los filtros
                            especialidades = todasLasRecetas
                                .map { it.medico.especialidad }
                                .distinct()
                                .sorted()

                            generarChipsFiltros()
                            mostrarRecetas(todasLasRecetas)
                        } else {
                            mostrarEstadoVacio()
                        }
                    } else {
                        Log.e("RecetasHistorial", "❌ Error: success = false")
                        mostrarEstadoVacio()
                    }
                } else {
                    Log.e("RecetasHistorial", "❌ Error HTTP: ${response.code()}")
                    Toast.makeText(
                        this@RecetasHistorialActivity,
                        "Error al cargar recetas: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    mostrarEstadoVacio()
                }
            }

            override fun onFailure(call: Call<RecetasResponse>, t: Throwable) {
                Log.e("RecetasHistorial", "❌ Error de red: ${t.message}", t)
                Toast.makeText(
                    this@RecetasHistorialActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
                mostrarEstadoVacio()
            }
        })
    }

    private fun generarChipsFiltros() {
        llFiltrosRecetas.removeAllViews()

        // Chip "Todas"
        val chipTodas = crearChip("Todas", null, true)
        llFiltrosRecetas.addView(chipTodas)

        // Chips de especialidades
        especialidades.forEach { especialidad ->
            val chip = crearChip(especialidad, especialidad, false)
            llFiltrosRecetas.addView(chip)
        }
    }

    private fun crearChip(texto: String, filtro: String?, seleccionado: Boolean): TextView {
        val chip = TextView(this).apply {
            text = texto
            textSize = 14f
            setPadding(48, 24, 48, 24)
            isClickable = true
            isFocusable = true

            // Estilo según si está seleccionado
            if (seleccionado) {
                setBackgroundResource(R.drawable.chip_selected)
                setTextColor(resources.getColor(android.R.color.white, null))
            } else {
                setBackgroundResource(R.drawable.chip_unselected)
                setTextColor(resources.getColor(R.color.primary_blue, null))
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = 24
            layoutParams = params

            setOnClickListener {
                seleccionarChip(this, filtro)
            }
        }
        return chip
    }

    private fun seleccionarChip(chipSeleccionado: TextView, filtro: String?) {
        // Deseleccionar todos los chips
        for (i in 0 until llFiltrosRecetas.childCount) {
            val child = llFiltrosRecetas.getChildAt(i) as TextView
            child.setBackgroundResource(R.drawable.chip_unselected)
            child.setTextColor(resources.getColor(R.color.primary_blue, null))
        }

        // Seleccionar el chip clickeado
        chipSeleccionado.setBackgroundResource(R.drawable.chip_selected)
        chipSeleccionado.setTextColor(resources.getColor(android.R.color.white, null))

        // Filtrar recetas
        filtrarRecetas(filtro)
    }

    private fun filtrarRecetas(especialidad: String?) {
        val recetasFiltradas = if (especialidad == null) {
            todasLasRecetas
        } else {
            todasLasRecetas.filter { it.medico.especialidad == especialidad }
        }

        mostrarRecetas(recetasFiltradas)
    }

    private fun mostrarRecetas(recetas: List<Receta>) {
        if (recetas.isNotEmpty()) {
            recetasAdapter.updateRecetas(recetas)
            rvRecetas.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
        } else {
            rvRecetas.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        }
    }

    private fun mostrarEstadoVacio() {
        rvRecetas.visibility = View.GONE
        emptyState.visibility = View.VISIBLE
    }

    private fun mostrarDetallesReceta(receta: Receta) {
        val bottomSheet = RecetaDetalleBottomSheet.newInstance(receta)
        bottomSheet.show(supportFragmentManager, "RecetaDetalleBottomSheet")
    }
}