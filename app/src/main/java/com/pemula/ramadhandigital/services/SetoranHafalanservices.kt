package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.SetoranHafalan
import com.pemula.ramadhandigital.model.SetoranHafalanResponse
import retrofit2.Response
import retrofit2.http.*

interface SetoranHafalanservices {
    // Siswa: Ambil setoran milik sendiri 🍌
    @GET("api/v1/setoran-hafalan/user/{idUser}")
    suspend fun getSetoranByUser(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int
    ): Response<SetoranHafalanResponse>

    // Guru: Ambil SEMUA setoran siswa untuk di-koreksi 🐒🔥
    @GET("api/v1/setoran-hafalan")
    suspend fun getAllSetoran(
        @Header("Authorization") token: String
    ): Response<SetoranHafalanResponse>

    // Guru: Update status setoran (Accept/Reject) 🍌🚀
    @PUT("api/v1/setoran-hafalan/{id}")
    suspend fun updateSetoran(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: SetoranHafalan
    ): Response<Map<String, Any>>

    // Simpan setoran baru
    @POST("api/v1/setoran-hafalan")
    suspend fun createSetoran(
        @Header("Authorization") token: String,
        @Body request: SetoranHafalan
    ): Response<Map<String, Any>>
}
