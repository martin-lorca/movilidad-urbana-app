package com.example.movilidadurbana.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.movilidadurbana.data.model.RegistroUsuarioRequest
import com.example.movilidadurbana.data.network.RetrofitClient
import com.example.movilidadurbana.databinding.ActivityRegisterBinding
import com.example.movilidadurbana.data.util.NetworkUtils
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val clave = binding.etClave.text.toString().trim()

            if (nombre.isNotEmpty() && email.isNotEmpty() && clave.isNotEmpty()) {
                registrarEnOracle(nombre, email, clave)
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnIrALogin.setOnClickListener { finish() }
    }

    private fun registrarEnOracle(nombre: String, email: String, clave: String) {
        if (!NetworkUtils.hayConexionInternet(this)) {
            Toast.makeText(this, "Sin conexión a Internet", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val request = RegistroUsuarioRequest(nombre, email, clave)
                val response = RetrofitClient.instance.registrarUsuario(request)

                if (response.isSuccessful && response.body()?.exito == true) {
                    Toast.makeText(this@RegisterActivity, "¡Usuario registrado con éxito en Oracle DB!", Toast.LENGTH_LONG).show()
                    finish() // Regresa a la pantalla de Login
                } else {
                    Toast.makeText(this@RegisterActivity, response.body()?.mensaje ?: "Error al registrar", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Excepción de Red al registrar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}