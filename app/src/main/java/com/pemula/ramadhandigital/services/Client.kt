package com.pemula.ramadhandigital.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Client {

    private const val BASE_URL = "http://192.168.69.127:3000/"

    private val http by lazy {
        Retrofit.Builder()
            .baseUrl(com.pemula.ramadhandigital.services.Client.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val auth: Authservices by lazy {
        com.pemula.ramadhandigital.services.Client.http.create(Authservices::class.java)
    }

}