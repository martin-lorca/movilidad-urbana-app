package com.example.movilidadurbana.data.util

import android.location.Location

object LocationHelper {
    // Calcula la distancia en kilómetros entre dos coordenadas GPS (Latitud, Longitud)
    fun calcularDistanciaKm(
        latOrigen: Double, lngOrigen: Double,
        latDestino: Double, lngDestino: Double
    ): Double {
        val origen = Location("origen").apply {
            latitude = latOrigen
            longitude = lngOrigen
        }
        val destino = Location("destino").apply {
            latitude = latDestino
            longitude = lngDestino
        }

        val metros = origen.distanceTo(destino)
        return (metros / 1000.0) // Convierte metros a kilómetros
    }
}