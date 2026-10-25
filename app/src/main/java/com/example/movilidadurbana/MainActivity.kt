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
    private val idUsuarioSesion = 1 // Usuario ID 1 (Ana Gómez)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Consultar saldo inicial al abrir la app (requiere red)
        obtenerPuntosServidor()

        // 2. Evento Registrar Viaje (valida red, calcula/procesa distancia y dispara TRIGGER en Oracle)
        binding.btnRegistrarViaje.setOnClickListener {
            val distanciaInput = binding.etDistancia.text.toString()
            val medioInput = binding.etIdMedio.text.toString()

            if (distanciaInput.isNotEmpty() && medioInput.isNotEmpty()) {
                val distanciaManual = distanciaInput.toDouble()
                val idMedio = medioInput.toInt()

                // Ejemplo de cálculo de coordenadas GPS fijas (Simulación Origen-Destino)
                // Coordenadas ej: Plaza de Armas a Parque Forestal (~1.5 km en línea recta)
                val latOrigen = -33.4372
                val lngOrigen = -70.6506
                val latDestino = -33.4350
                val lngDestino = -70.6400

                val distanciaCalculadaGps = LocationHelper.calcularDistanciaKm(
                    latOrigen, lngOrigen, latDestino, lngDestino
                )

                // Si el usuario ingresó un valor se usa ese, de lo contrario se toma el del GPS
                val distanciaFinal = if (distanciaManual > 0) distanciaManual else distanciaCalculadaGps

                registrarViajeEnOracle(idMedio, distanciaFinal)
            } else {
                Toast.makeText(this, "Complete la distancia y el medio de transporte", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Eventos para Canjes (ejecutan PACKAGE en Oracle con validación de red)
        binding.btnCanjearDescuento.setOnClickListener {
            procesarCanjeIncentivo(idIncentivo = 1)
        }

        binding.btnCanjearPase.setOnClickListener {
            procesarCanjeIncentivo(idIncentivo = 2)
        }
    }

    private fun obtenerPuntosServidor() {
        // Verificar conexión a Internet activa antes de consultar la API
        if (!NetworkUtils.hayConexionInternet(this)) {
            binding.tvPuntosAcumulados.text = "Sin conexión a Internet"
            Toast.makeText(this, "Se requiere conexión a Internet para sincronizar con Oracle Cloud", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.obtenerPuntos(idUsuarioSesion)
                if (response.isSuccessful && response.body() != null) {
                    val puntos = response.body()!!.puntos
                    binding.tvPuntosAcumulados.text = "$puntos pts"
                } else {
                    binding.tvPuntosAcumulados.text = "Error al consultar saldo"
                }
            } catch (e: Exception) {
                binding.tvPuntosAcumulados.text = "Error de conexión"
            }
        }
    }

    private fun registrarViajeEnOracle(idMedio: Int, distanciaKm: Double) {
        // Validar que exista red activa antes de llamar a Oracle Cloud
        if (!NetworkUtils.hayConexionInternet(this)) {
            Toast.makeText(this, "Sin conexión a Internet. Conéctate para guardar el viaje en la base de datos.", Toast.LENGTH_LONG).show()
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
                    Toast.makeText(
                        this@MainActivity,
                        "¡Viaje de ${String.format("%.2f", distanciaKm)} KM registrado! Trigger ejecutado en Oracle.",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.etDistancia.text?.clear()

                    // Actualizar los puntos recalculados automáticamente por el Trigger
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
        // Validar red antes de invocar el procedimiento de canje
        if (!NetworkUtils.hayConexionInternet(this)) {
            Toast.makeText(this, "Sin conexión a Internet. Conéctate para canjear tu recompensa.", Toast.LENGTH_LONG).show()
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
                    Toast.makeText(
                        this@MainActivity,
                        "No se pudo procesar (Puntos o Stock insuficiente)",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error de red al procesar canje", Toast.LENGTH_SHORT).show()
            }
        }
    }
}