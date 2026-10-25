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

interface ApiService {

    // 1. Obtener puntos acumulados invocando pkg_gestion_movilidad.fn_obtener_puntos
    @GET("usuarios/{id}/puntos")
    suspend fun obtenerPuntos(
        @Path("id") idUsuario: Int
    ): Response<PuntosResponse>

    // 2. Registrar viaje ejecutando sp_registrar_viaje (dispara el Trigger en Oracle Cloud)
    @POST("viajes/registrar")
    suspend fun registrarViaje(
        @Body request: ViajeRequest
    ): Response<ApiResponse>

    // 3. Canjear premio ejecutando pkg_gestion_movilidad.sp_canjear_incentivo
    @POST("incentivos/canjear")
    suspend fun canjearIncentivo(
        @Body request: CanjeRequest
    ): Response<ApiResponse>
}