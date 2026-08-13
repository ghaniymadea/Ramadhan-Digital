package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.model.IbadahHarianResponse
import retrofit2.Response
import retrofit2.http.*

interface IbadahHarianservices {
    // Ambil log ibadah harian berdasarkan ID User 🍌
    @GET("api/v1/ibadah-harian/user/{idUser}")
    suspend fun getIbadahHarian(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int
    ): Response<IbadahHarianResponse>

    // Simpan catatan ibadah harian baru 🐒
    @POST("api/v1/ibadah-harian")
    suspend fun registerIbadahHarian(
        @Header("Authorization") token: String,
        @Body request: IbadahHarian
    ): Response<IbadahHarianResponse>
}
