package com.example.movilidadurbana.data.model

import com.google.gson.annotations.SerializedName

// Petición de Login
data class LoginRequest(
    @SerializedName("p_email") val email: String,
    @SerializedName("p_clave") val clave: String
)

// Respuesta de Login
data class LoginResponse(
    @SerializedName("exito") val exito: Boolean,
    @SerializedName("mensaje") val mensaje: String?,
    @SerializedName("id_usuario") val idUsuario: Int?,
    @SerializedName("nombre") val nombre: String?
)

// Petición de Registro de Usuario
data class RegistroUsuarioRequest(
    @SerializedName("p_nombre") val nombre: String,
    @SerializedName("p_email") val email: String,
    @SerializedName("p_clave") val clave: String
)

// Modelos anteriores
data class ViajeRequest(
    @SerializedName("p_id_usuario") val idUsuario: Int,
    @SerializedName("p_id_medio") val idMedio: Int,
    @SerializedName("p_distancia_km") val distanciaKm: Double,
    @SerializedName("p_paradas") val paradasRuta: List<String>
)

data class PuntosResponse(
    @SerializedName("puntos") val puntos: Int
)

data class CanjeRequest(
    @SerializedName("p_id_usuario") val idUsuario: Int,
    @SerializedName("p_id_incentivo") val idIncentivo: Int
)

data class ApiResponse(
    @SerializedName("exito") val exito: Boolean,
    @SerializedName("mensaje") val mensaje: String?
)