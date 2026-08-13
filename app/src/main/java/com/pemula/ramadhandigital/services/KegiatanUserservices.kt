package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.KegiatanRegisterResponse
import com.pemula.ramadhandigital.model.KegiatanResponse
import com.pemula.ramadhandigital.model.KegiatanUser
import com.pemula.ramadhandigital.model.KegiatanUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface KegiatanUserservices {
    // Ambil SEMUA kegiatan master 🍌
    @GET("api/v1/kegiatan")
    suspend fun getAllKegiatan(
        @Header("Authorization") token: String
    ): Response<KegiatanResponse>

    // Ambil kegiatan khusus USER (yang sudah ada catatannya) 🐒
    @GET("api/v1/kegiatan/user/{idUser}")
    suspend fun getKegiatanByUser(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int
    ): Response<KegiatanUserResponse>

    // Simpan atau Daftar kegiatan baru dengan catatan 🍌
    @POST("api/v1/kegiatan/register")
    suspend fun registerKegiatan(
        @Header("Authorization") token: String,
        @Body request: KegiatanUser
    ): Response<KegiatanRegisterResponse>
}
