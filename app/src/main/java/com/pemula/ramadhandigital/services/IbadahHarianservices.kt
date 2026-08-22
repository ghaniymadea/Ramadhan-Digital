package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.model.SingleIbadahHarianResponse
import retrofit2.Response
import retrofit2.http.*

interface IbadahHarianservices {
    @GET("api/v1/ibadah-harian")
    suspend fun getIbadahHarian(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String
    ): Response<SingleIbadahHarianResponse>

    @POST("api/v1/ibadah-harian")
    suspend fun registerIbadahHarian(
        @Header("Authorization") token: String,
        @Body request: IbadahHarian
    ): Response<Map<String, Any>>
}
