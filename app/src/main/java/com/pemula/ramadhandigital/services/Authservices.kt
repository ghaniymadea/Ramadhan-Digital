package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.Login
import com.pemula.ramadhandigital.model.LoginRespons
import com.pemula.ramadhandigital.model.RegisterGuru
import com.pemula.ramadhandigital.model.RegisterGuruResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Authservices {

    @POST("api/v1/auth/login")
    suspend fun Login_services(
        @Body request: Login
    ): Response<LoginRespons>

    // API Register Guru sesuai Postman Bos! 🍌🐒
    @POST("api/v1/auth/register-guru")
    suspend fun registerGuru(
        @Body request: RegisterGuru
    ): Response<RegisterGuruResponse>
}
