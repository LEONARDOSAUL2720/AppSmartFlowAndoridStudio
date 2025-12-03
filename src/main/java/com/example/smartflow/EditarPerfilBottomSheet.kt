package com.example.smartflow

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import coil.transform.CircleCropTransformation
import com.example.smartflow.data.api.RetrofitClient
import com.example.smartflow.data.models.ActualizarPerfilRequest
import com.example.smartflow.data.models.ActualizarPerfilResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarPerfilBottomSheet : BottomSheetDialogFragment() {

    private lateinit var ivFotoPerfil: ImageView
    private lateinit var btnCambiarFoto: ImageButton
    private lateinit var tvCambiarFoto: TextView
    private lateinit var etNombre: TextInputEditText
    private lateinit var etApellido: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etPasswordConfirm: TextInputEditText
    private lateinit var btnGuardarCambios: MaterialButton

    private var selectedImageUri: Uri? = null

    // Launcher para seleccionar imagen de la galería
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                // Mostrar la imagen seleccionada
                ivFotoPerfil.load(uri) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
                Toast.makeText(requireContext(), "Foto seleccionada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Estilo para hacer el bottom sheet más grande
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_editar_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        initViews(view)

        // Cargar datos actuales del usuario
        cargarDatosActuales()

        // Configurar listeners
        setupListeners()
    }

    private fun initViews(view: View) {
        ivFotoPerfil = view.findViewById(R.id.iv_foto_perfil)
        btnCambiarFoto = view.findViewById(R.id.btn_cambiar_foto)
        tvCambiarFoto = view.findViewById(R.id.tv_cambiar_foto)
        etNombre = view.findViewById(R.id.et_nombre)
        etApellido = view.findViewById(R.id.et_apellido)
        etEmail = view.findViewById(R.id.et_email)
        etTelefono = view.findViewById(R.id.et_telefono)
        etPassword = view.findViewById(R.id.et_password)
        etPasswordConfirm = view.findViewById(R.id.et_password_confirm)
        btnGuardarCambios = view.findViewById(R.id.btn_guardar_cambios)
    }

    private fun setupListeners() {
        // Botón cerrar (opcional)
        view?.findViewById<ImageButton>(R.id.btn_close_bottom_sheet)?.setOnClickListener {
            dismiss()
        }

        // Botón cambiar foto
        btnCambiarFoto.setOnClickListener {
            abrirGaleria()
        }

        // Texto "Cambiar Foto" también abre la galería
        tvCambiarFoto.setOnClickListener {
            abrirGaleria()
        }

        // Botón guardar cambios
        btnGuardarCambios.setOnClickListener {
            validarYGuardarCambios()
        }
    }

    private fun cargarDatosActuales() {
        val prefs = requireContext().getSharedPreferences("app_prefs", Activity.MODE_PRIVATE)

        val nombre = prefs.getString("user_nombre", "") ?: ""
        val apellido = prefs.getString("user_apellido", "") ?: ""
        val email = prefs.getString("user_email", "") ?: ""
        val telefono = prefs.getString("user_telefono", "") ?: ""
        val foto = prefs.getString("user_foto", null)

        // Pre-llenar los campos
        etNombre.setText(nombre)
        etApellido.setText(apellido)
        etEmail.setText(email)
        etTelefono.setText(telefono)

        // Cargar foto actual
        if (!foto.isNullOrEmpty()) {
            ivFotoPerfil.load(foto) {
                crossfade(true)
                placeholder(R.drawable.ic_user_placeholder)
                error(R.drawable.ic_user_placeholder)
                transformations(CircleCropTransformation())
            }
        }

        Log.d("EditarPerfilBS", "Datos cargados - Nombre: $nombre, Apellido: $apellido")
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    private fun validarYGuardarCambios() {
        // Obtener valores de los campos
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val passwordConfirm = etPasswordConfirm.text.toString().trim()

        // Validaciones
        if (nombre.isEmpty()) {
            etNombre.error = "El nombre es obligatorio"
            etNombre.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "El email es obligatorio"
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email inválido"
            etEmail.requestFocus()
            return
        }

        if (telefono.isEmpty()) {
            etTelefono.error = "El teléfono es obligatorio"
            etTelefono.requestFocus()
            return
        }

        if (!telefono.matches(Regex("^\\d{10}$"))) {
            etTelefono.error = "El teléfono debe tener 10 dígitos"
            etTelefono.requestFocus()
            return
        }

        // ✅ NUEVAS VALIDACIONES DE CONTRASEÑA
        // Si el usuario ingresó una contraseña
        if (password.isNotEmpty()) {
            // Validar longitud mínima
            if (password.length < 6) {
                etPassword.error = "La contraseña debe tener al menos 6 caracteres"
                etPassword.requestFocus()
                return
            }

            // Validar que haya confirmación
            if (passwordConfirm.isEmpty()) {
                etPasswordConfirm.error = "Confirma tu nueva contraseña"
                etPasswordConfirm.requestFocus()
                return
            }

            // Validar que coincidan
            if (password != passwordConfirm) {
                etPasswordConfirm.error = "Las contraseñas no coinciden"
                etPasswordConfirm.requestFocus()
                Toast.makeText(
                    requireContext(),
                    "Las contraseñas no coinciden",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        } else {
            // Si no ingresó contraseña pero sí confirmación
            if (passwordConfirm.isNotEmpty()) {
                etPassword.error = "Ingresa la nueva contraseña"
                etPassword.requestFocus()
                return
            }
        }

        // Si todas las validaciones pasan, guardar cambios
        guardarCambios(nombre, apellido, email, telefono, password.ifEmpty { null })
    }

    private fun guardarCambios(
        nombre: String,
        apellido: String,
        email: String,
        telefono: String,
        password: String?
    ) {
        // Obtener token de SharedPreferences
        val prefs = requireContext().getSharedPreferences("app_prefs", Activity.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Error: No se encontró el token de autenticación", Toast.LENGTH_LONG).show()
            return
        }

        // Deshabilitar botón mientras se guarda
        btnGuardarCambios.isEnabled = false
        btnGuardarCambios.text = "Guardando..."

        // ✅ NUEVO: Convertir imagen a Base64 si el usuario seleccionó una
        val fotoBase64 = if (selectedImageUri != null) {
            Log.d("EditarPerfilBS", "Convirtiendo imagen a Base64...")
            convertImageToBase64(selectedImageUri!!)
        } else {
            null
        }

        // Crear request (AHORA INCLUYENDO LA FOTO)
        val request = ActualizarPerfilRequest(
            nombre = nombre,
            apellido = apellido.ifEmpty { null },
            email = email,
            telefono = telefono,
            password = password,
            foto = fotoBase64  // ✅ AGREGAR FOTO
        )

        Log.d("EditarPerfilBS", "Enviando actualización con token")
        Log.d("EditarPerfilBS", "Foto incluida: ${if (fotoBase64 != null) "Sí (${fotoBase64.length} chars)" else "No"}")
        Log.d("EditarPerfilBS", "Request: $request")

        // Llamar al backend CON TOKEN
        RetrofitClient.apiService.actualizarPerfil("Bearer $token", request)
            .enqueue(object : Callback<ActualizarPerfilResponse> {
                override fun onResponse(
                    call: Call<ActualizarPerfilResponse>,
                    response: Response<ActualizarPerfilResponse>
                ) {
                    btnGuardarCambios.isEnabled = true
                    btnGuardarCambios.text = "Guardar Cambios"

                    if (response.isSuccessful && response.body()?.success == true) {
                        val userData = response.body()?.user
                        Log.d("EditarPerfilBS", "✅ Perfil actualizado exitosamente")
                        Log.d("EditarPerfilBS", "Foto recibida del backend: ${userData?.foto?.take(100)}...")

                        userData?.let { user ->
                            actualizarSharedPreferences(
                                user.nombre,
                                user.apellido,
                                user.email,
                                user.telefono,
                                user.foto
                            )
                        }

                        Toast.makeText(
                            requireContext(),
                            "Perfil actualizado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Notificar al Activity padre que se actualizaron los datos
                        (activity as? PacientePerfilActivity)?.cargarDatosUsuario()

                        // Cerrar el bottom sheet
                        dismiss()
                    } else {
                        val errorMsg = response.body()?.message ?: "Error al actualizar perfil"
                        Log.e("EditarPerfilBS", "❌ Error: $errorMsg - Code: ${response.code()}")
                        Toast.makeText(
                            requireContext(),
                            errorMsg,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ActualizarPerfilResponse>, t: Throwable) {
                    btnGuardarCambios.isEnabled = true
                    btnGuardarCambios.text = "Guardar Cambios"

                    Log.e("EditarPerfilBS", "❌ Error de red: ${t.message}", t)
                    Toast.makeText(
                        requireContext(),
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun actualizarSharedPreferences(
        nombre: String?,
        apellido: String?,
        email: String?,
        telefono: String?,
        foto: String?
    ) {
        val prefs = requireContext().getSharedPreferences("app_prefs", Activity.MODE_PRIVATE)

        prefs.edit().apply {
            // Usar valores por defecto si son null
            putString("user_nombre", nombre ?: "")
            putString("user_apellido", apellido ?: "")
            putString("user_email", email ?: "")
            putString("user_telefono", telefono ?: "")
            if (foto != null) {
                putString("user_foto", foto)
            }
            apply()
        }

        Log.d("EditarPerfilBS", "✅ SharedPreferences actualizado")
        Log.d("EditarPerfilBS", "Nueva foto guardada: ${foto?.substring(0, 50)}...")  // ✅ Agrega este log
    }

    private fun convertImageToBase64(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                // Comprimir la imagen antes de convertir a Base64
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                val stream = java.io.ByteArrayOutputStream()

                // Comprimir a JPEG con calidad 70% y max 800x800
                val maxSize = 800
                val ratio = Math.min(
                    maxSize.toFloat() / bitmap.width,
                    maxSize.toFloat() / bitmap.height
                )
                val width = (ratio * bitmap.width).toInt()
                val height = (ratio * bitmap.height).toInt()

                val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, width, height, true)
                scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, stream)

                val imageBytes = stream.toByteArray()
                val base64 = android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_WRAP)

                bitmap.recycle()
                scaledBitmap.recycle()

                "data:image/jpeg;base64,$base64"
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("EditarPerfilBS", "Error convirtiendo imagen a Base64", e)
            null
        }
    }



    companion object {
        const val TAG = "EditarPerfilBottomSheet"

        fun newInstance(): EditarPerfilBottomSheet {
            return EditarPerfilBottomSheet()
        }
    }
}