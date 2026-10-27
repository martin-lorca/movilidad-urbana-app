package com.example.movilidadurbana

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.movilidadurbana.data.model.CanjeRequest
import com.example.movilidadurbana.data.model.ViajeRequest
import com.example.movilidadurbana.data.network.RetrofitClient
import com.example.movilidadurbana.databinding.ActivityMainBinding
import com.example.movilidadurbana.data.util.LocationHelper
import com.example.movilidadurbana.data.util.NetworkUtils
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var idUsuarioSesion = 1
    private var nombreUsuarioSesion = "Usuario"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del usuario autenticado
        idUsuarioSesion = intent.getIntExtra("EXTRA_ID_USUARIO", 1)
        nombreUsuarioSesion = intent.getStringExtra("EXTRA_NOMBRE_USUARIO") ?: "Usuario"

        // Actualizar saludo
        binding.tvPuntosAcumulados.text = getString(R.string.loading_points)

        obtenerPuntosServidor()

        binding.btnRegistrarViaje.setOnClickListener {
            val distanciaInput = binding.etDistancia.text.toString()
            val medioInput = binding.etIdMedio.text.toString()

            if (distanciaInput.isNotEmpty() && medioInput.isNotEmpty()) {
                val distanciaManual = distanciaInput.toDouble()
                val idMedio = medioInput.toInt()

                val latOrigen = -33.4372
                val lngOrigen = -70.6506
                val latDestino = -33.4350
                val lngDestino = -70.6400

                val distanciaCalculadaGps = LocationHelper.calcularDistanciaKm(
                    latOrigen, lngOrigen, latDestino, lngDestino
                )

                val distanciaFinal = if (distanciaManual > 0) distanciaManual else distanciaCalculadaGps
                registrarViajeEnOracle(idMedio, distanciaFinal)
            } else {
                Toast.makeText(this, "Complete la distancia y el medio de transporte", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCanjearDescuento.setOnClickListener { procesarCanjeIncentivo(idIncentivo = 1) }
        binding.btnCanjearPase.setOnClickListener { procesarCanjeIncentivo(idIncentivo = 2) }
    }

    private fun obtenerPuntosServidor() {
        if (!NetworkUtils.hayConexionInternet(this)) {
            binding.tvPuntosAcumulados.text = "Sin conexión a Internet"
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.obtenerPuntos(idUsuarioSesion)
                if (response.isSuccessful && response.body() != null) {
                    val puntos = response.body()!!.puntos
                    binding.tvPuntosAcumulados.text = "$puntos pts ($nombreUsuarioSesion)"
                } else {
                    binding.tvPuntosAcumulados.text = "Error al consultar saldo"
                }
            } catch (e: Exception) {
                binding.tvPuntosAcumulados.text = "Error de conexión"
            }
        }
    }

    private fun registrarViajeEnOracle(idMedio: Int, distanciaKm: Double) {
        if (!NetworkUtils.hayConexionInternet(this)) {
            Toast.makeText(this, "Sin conexión a Internet", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            try {
                val request = ViajeRequest(
                    idUsuario = idUsuarioSesion,
                    idMedio = idMedio,
                    distanciaKm = distanciaKm,
                    paradasRuta = listOf("GPS Origen", "GPS Destino")
                )

                val response = RetrofitClient.instance.registrarViaje(request)
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "¡Viaje registrado! Trigger ejecutado en Oracle.", Toast.LENGTH_LONG).show()
                    binding.etDistancia.text?.clear()
                    obtenerPuntosServidor()
                } else {
                    Toast.makeText(this@MainActivity, "Error en la transacción de Oracle", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Excepción de Red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun procesarCanjeIncentivo(idIncentivo: Int) {
        if (!NetworkUtils.hayConexionInternet(this)) {
            Toast.makeText(this, "Sin conexión a Internet", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            try {
                val request = CanjeRequest(idUsuarioSesion, idIncentivo)
                val response = RetrofitClient.instance.canjearIncentivo(request)

                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "¡Canje exitoso procesado por Package!", Toast.LENGTH_LONG).show()
                    obtenerPuntosServidor()
                } else {
                    Toast.makeText(this@MainActivity, "No se pudo procesar (Puntos/Stock insuficiente)", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error de red al procesar canje", Toast.LENGTH_SHORT).show()
            }
        }
    }
}