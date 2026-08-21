package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.Kegiatan
import com.pemula.ramadhandigital.model.KegiatanRegisterResponse
import com.pemula.ramadhandigital.model.KegiatanResponse
import com.pemula.ramadhandigital.model.KegiatanUser
import com.pemula.ramadhandigital.model.KegiatanUserResponse
import retrofit2.Response
import retrofit2.http.*

interface KegiatanUserservices {

    // ---- SISWA ----
    
    // GET /api/v1/kegiatan -> Ambil SEMUA kegiatan master 🍌
    @GET("api/v1/kegiatan")
    suspend fun getAllKegiatan(
        @Header("Authorization") token: String
    ): Response<KegiatanResponse>

    // GET /api/v1/kegiatan/{id} -> Ambil detail kegiatan master by ID 🐒
    @GET("api/v1/kegiatan/{id}")
    suspend fun getKegiatanById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<KegiatanResponse>

    // POST /api/v1/kegiatan/register -> Simpan catatan kegiatan siswa 🚀
    @POST("api/v1/kegiatan/register")
    suspend fun registerKegiatan(
        @Header("Authorization") token: String,
        @Body request: KegiatanUser
    ): Response<KegiatanRegisterResponse>

    // ---- GURU ----

    // GET /api/v1/kegiatan/user/{idUser} -> Ambil riwayat kegiatan milik user tertentu 🧐
    @GET("api/v1/kegiatan/user/{idUser}")
    suspend fun getKegiatanByUser(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int
    ): Response<KegiatanUserResponse>

    // ---- ADMIN / GURU (MANAGEMENT) ----

    @POST("api/v1/kegiatan")
    suspend fun createKegiatan(
        @Header("Authorization") token: String,
        @Body kegiatan: Kegiatan
    ): Response<KegiatanRegisterResponse>

    @DELETE("api/v1/kegiatan/{id}")
    suspend fun deleteKegiatan(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<KegiatanRegisterResponse>
}
