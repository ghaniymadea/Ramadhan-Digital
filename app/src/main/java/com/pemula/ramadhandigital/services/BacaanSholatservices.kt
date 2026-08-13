package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.BacaanSholat
import retrofit2.Response
import retrofit2.http.GET

interface BacaanSholatservices {
    
    @GET("api/v1/bacaan-sholat")
    suspend fun getBacaanSholat(): Response<List<BacaanSholat>>

}