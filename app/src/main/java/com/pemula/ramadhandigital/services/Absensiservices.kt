package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.AbsensiResponse
import com.pemula.ramadhandigital.model.PostAbsensiRequest
import com.pemula.ramadhandigital.model.PostAbsensiResponse
import retrofit2.Response
import retrofit2.http.*

interface Absensiservices {
    // GET: /api/v1/absensi/kelas/{idKelas} 🍌🐒
    @GET("api/v1/absensi/kelas/{idKelas}")
    suspend fun getAbsensi(
        @Header("Authorization") token: String,
        @Path("idKelas") idKelas: Int,
        @Query("tanggal") tanggal: String?
    ): Response<AbsensiResponse>

    // POST: /api/v1/absensi/kelas 🚀🔥
    @POST("api/v1/absensi/kelas")
    suspend fun postAbsensi(
        @Header("Authorization") token: String,
        @Body request: PostAbsensiRequest
    ): Response<PostAbsensiResponse>
}
