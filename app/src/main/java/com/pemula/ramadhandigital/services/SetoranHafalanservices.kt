package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.SetoranHafalan
import com.pemula.ramadhandigital.model.SetoranHafalanResponse
import retrofit2.Response
import retrofit2.http.*

interface SetoranHafalanservices {
    // Ambil semua setoran hafalan milik user tertentu 🍌
    @GET("api/v1/setoran-hafalan/user/{idUser}")
    suspend fun getSetoranByUser(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int
    ): Response<SetoranHafalanResponse>

    // Simpan setoran hafalan baru 🐒🔥
    @POST("api/v1/setoran-hafalan")
    suspend fun createSetoran(
        @Header("Authorization") token: String,
        @Body request: SetoranHafalan
    ): Response<Map<String, Any>>
}
