package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.SetoranHafalan
import com.pemula.ramadhandigital.model.SetoranHafalanResponse
import com.pemula.ramadhandigital.model.GenericResponse
import retrofit2.Response
import retrofit2.http.*

interface SetoranHafalanservices {

    // GET /api/v1/setoran-hafalan/ -> Guru ambil semua, Siswa ambil miliknya (tergantung filter backend)
    @GET("api/v1/setoran-hafalan/")
    suspend fun getAllSetoran(
        @Header("Authorization") token: String
    ): Response<SetoranHafalanResponse>

    // POST /api/v1/setoran-hafalan/ -> Membuat setoran baru
    @POST("api/v1/setoran-hafalan/")
    suspend fun createSetoran(
        @Header("Authorization") token: String,
        @Body request: SetoranHafalan
    ): Response<GenericResponse>

    // PUT /api/v1/setoran-hafalan/{id} -> Guru update status (Lulus/Tidak)
    @PUT("api/v1/setoran-hafalan/{id}")
    suspend fun updateSetoran(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: SetoranHafalan
    ): Response<GenericResponse>

    // GET /api/v1/setoran-hafalan/kelas/{idKelas} -> Monitoring Guru per kelas
    @GET("api/v1/setoran-hafalan/kelas/{idKelas}")
    suspend fun getSetoranByKelas(
        @Header("Authorization") token: String,
        @Path("idKelas") idKelas: Int
    ): Response<SetoranHafalanResponse>

    @DELETE("api/v1/setoran-hafalan/{id}")
    suspend fun deleteSetoran(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<GenericResponse>
}