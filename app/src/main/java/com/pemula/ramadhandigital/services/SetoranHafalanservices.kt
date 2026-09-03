package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.SetoranHafalan
import com.pemula.ramadhandigital.model.SetoranHafalanResponse
import com.pemula.ramadhandigital.model.GenericResponse
import retrofit2.Response
import retrofit2.http.*

interface SetoranHafalanservices {

    // ========================================================
    // SETORAN SURAH
    // ========================================================

    @GET("api/v1/setoran-surah/")
    suspend fun getAllSetoranSurah(
        @Header("Authorization") token: String
    ): Response<SetoranHafalanResponse>

    @GET("api/v1/setoran-surah/{id}")
    suspend fun getSetoranSurahById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<SetoranHafalanResponse>

    @GET("api/v1/setoran-surah/user/{userId}")
    suspend fun getSetoranSurahByUserId(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<SetoranHafalanResponse>

    @POST("api/v1/setoran-surah/")
    suspend fun createSetoranSurah(
        @Header("Authorization") token: String,
        @Body request: SetoranHafalan
    ): Response<GenericResponse>

    @PUT("api/v1/setoran-surah/{id}")
    suspend fun updateSetoranSurah(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: SetoranHafalan
    ): Response<GenericResponse>

    @DELETE("api/v1/setoran-surah/{id}")
    suspend fun deleteSetoranSurah(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<GenericResponse>


    // ========================================================
    // SETORAN BACAAN SHOLAT
    // ========================================================

    @GET("api/v1/setoran-bacaan-sholat/")
    suspend fun getAllSetoranBacaanSholat(
        @Header("Authorization") token: String
    ): Response<SetoranHafalanResponse>

    @GET("api/v1/setoran-bacaan-sholat/{id}")
    suspend fun getSetoranBacaanSholatById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<SetoranHafalanResponse>

    @GET("api/v1/setoran-bacaan-sholat/user/{userId}")
    suspend fun getSetoranBacaanSholatByUserId(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<SetoranHafalanResponse>

    @POST("api/v1/setoran-bacaan-sholat/")
    suspend fun createSetoranBacaanSholat(
        @Header("Authorization") token: String,
        @Body request: SetoranHafalan
    ): Response<GenericResponse>

    @PUT("api/v1/setoran-bacaan-sholat/{id}")
    suspend fun updateSetoranBacaanSholat(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: SetoranHafalan
    ): Response<GenericResponse>

    @DELETE("api/v1/setoran-bacaan-sholat/{id}")
    suspend fun deleteSetoranBacaanSholat(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<GenericResponse>
}