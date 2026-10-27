package com.example.movilidadurbana.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.movilidadurbana.MainActivity
import com.example.movilidadurbana.data.model.LoginRequest
import com.example.movilidadurbana.data.network.RetrofitClient
import com.example.movilidadurbana.databinding.ActivityLoginBinding
import com.example.movilidadurbana.data.util.NetworkUtils
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val clave = binding.etClave.text.toString().trim()

            if (email.isNotEmpty() && clave.isNotEmpty()) {
                procesarLogin(email, clave)
            } else {
                Toast.makeText(this, "Ingresa tu email y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnIrARegistro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun procesarLogin(email: String, clave: String) {
        if (!NetworkUtils.hayConexionInternet(this)) {
            Toast.makeText(this, "Sin conexión a Internet", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, clave))
                if (response.isSuccessful && response.body()?.exito == true) {
                    val idUsuario = response.body()?.idUsuario ?: 1
                    val nombre = response.body()?.nombre ?: "Usuario"

                    val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                        putExtra("EXTRA_ID_USUARIO", idUsuario)
                        putExtra("EXTRA_NOMBRE_USUARIO", nombre)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, response.body()?.mensaje ?: "Credenciales inválidas", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error de comunicación con Oracle", Toast.LENGTH_SHORT).show()
            }
        }
    }
}