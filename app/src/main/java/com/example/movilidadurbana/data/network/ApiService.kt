package com.example.movilidadurbana.data.network

import com.example.movilidadurbana.data.model.ApiResponse
import com.example.movilidadurbana.data.model.CanjeRequest
import com.example.movilidadurbana.data.model.PuntosResponse
import com.example.movilidadurbana.data.model.ViajeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import com.example.movilidadurbana.data.model.*

interface ApiService {

    // 1. Iniciar Sesión
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // 2. Registrar Nuevo Usuario en Oracle DB
    @POST("auth/registrar")
    suspend fun registrarUsuario(
        @Body request: RegistroUsuarioRequest
    ): Response<ApiResponse>

    // 3. Obtener puntos acumulados
    @GET("usuarios/{id}/puntos")
    suspend fun obtenerPuntos(
        @Path("id") idUsuario: Int
    ): Response<PuntosResponse>

    // 4. Registrar viaje
    @POST("viajes/registrar")
    suspend fun registrarViaje(
        @Body request: ViajeRequest
    ): Response<ApiResponse>

    // 5. Canjear premio
    @POST("incentivos/canjear")
    suspend fun canjearIncentivo(
        @Body request: CanjeRequest
    ): Response<ApiResponse>
}