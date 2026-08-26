package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.KegiatanRegisterResponse
import com.pemula.ramadhandigital.model.KegiatanResponse
import com.pemula.ramadhandigital.model.KegiatanUser
import com.pemula.ramadhandigital.model.KegiatanUserResponse
import retrofit2.Response
import retrofit2.http.*

interface KegiatanUserservices {

    //----GURU----
    // GET by User ID
    @GET("api/v1/kegiatan/user/{idUser}")
    suspend fun getKegiatanByUser(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int
    ): Response<KegiatanUserResponse>

    //----SISWA----
    // POST MENGISI KEGIATAN (Register)
    @POST("api/v1/kegiatan/register")
    suspend fun registerKegiatan(
        @Header("Authorization") token: String,
        @Body request: KegiatanUser
    ): Response<KegiatanRegisterResponse>

    // GET ALL
    @GET("api/v1/kegiatan")
    suspend fun getAllKegiatan(
        @Header("Authorization") token: String
    ): Response<KegiatanResponse>

    // GET by ID
    @GET("api/v1/kegiatan/{id}")
    suspend fun getKegiatanById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<KegiatanResponse>
}
