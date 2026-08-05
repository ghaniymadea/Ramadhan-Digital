package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.Login
import com.pemula.ramadhandigital.model.LoginRespons
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Authservices {

    @POST("api/v1/auth/login")
    suspend fun Login_services(
        @Body request: Login
    ): Response<LoginRespons>
}