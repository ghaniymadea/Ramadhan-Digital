package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.Dzikir
import retrofit2.Response
import retrofit2.http.GET


interface Dzikirservices {
    @GET("api/v1/dzikir")
    suspend fun getDzikir(): Response<List<Dzikir>>
}