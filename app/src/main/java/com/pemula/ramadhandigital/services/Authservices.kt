package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.*
import retrofit2.Response
import retrofit2.http.*

interface Authservices {

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: Login
    ): Response<LoginRespons>

    @POST("api/v1/auth/register-admin")
    suspend fun registerAdmin(
        @Body request: RegisterRequest
    ): Response<CommonResponse>

    // Endpoint yang diproteksi Policies.Admin 🔐
    
    @POST("api/v1/auth/register-guru")
    suspend fun registerGuru(
        @Header("Authorization") token: String,
        @Body request: RegisterRequest
    ): Response<CommonResponse>

    @POST("api/v1/auth/register-siswa")
    suspend fun registerSiswa(
        @Header("Authorization") token: String,
        @Body request: RegisterRequest
    ): Response<CommonResponse>

    @GET("api/v1/auth/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): Response<Map<String, Any>>
}
