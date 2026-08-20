package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.Tausiah
import com.pemula.ramadhandigital.model.TausiahResponse
import retrofit2.Response
import retrofit2.http.*

interface Tausiahservices {
    // Ambil semua daftar tausiah 🍌
    @GET("api/v1/tausiah")
    suspend fun getAllTausiah(
        @Header("Authorization") token: String
    ): Response<TausiahResponse>

    // Simpan catatan tausiah baru (Kirim objek lengkap) 🐒🔥
    @POST("api/v1/tausiah")
    suspend fun createTausiah(
        @Header("Authorization") token: String,
        @Body request: Tausiah
    ): Response<Map<String, Any>>
}
