package com.example.movilidadurbana.data.model

import com.google.gson.annotations.SerializedName

// Petición para registrar un viaje (ejecuta sp_registrar_viaje y activa el TRIGGER)
data class ViajeRequest(
    @SerializedName("p_id_usuario") val idUsuario: Int,
    @SerializedName("p_id_medio") val idMedio: Int,
    @SerializedName("p_distancia_km") val distanciaKm: Double,
    @SerializedName("p_paradas") val paradasRuta: List<String>
)

// Respuesta para consultar puntos (invoca fn_obtener_puntos)
data class PuntosResponse(
    @SerializedName("puntos") val puntos: Int
)

// Petición para canjear un incentivo (ejecuta pkg_gestion_movilidad.sp_canjear_incentivo)
data class CanjeRequest(
    @SerializedName("p_id_usuario") val idUsuario: Int,
    @SerializedName("p_id_incentivo") val idIncentivo: Int
)

// Respuesta estándar
data class ApiResponse(
    @SerializedName("exito") val exito: Boolean,
    @SerializedName("mensaje") val mensaje: String?
)