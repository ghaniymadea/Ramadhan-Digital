package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.IbadahSunnahResponse
import com.pemula.ramadhandigital.model.SaveIbadahSunnahRequest
import retrofit2.Response
import retrofit2.http.*

interface IbadahSunnahservices {
    // Ambil data ibadah sunnah milik user sendiri berdasarkan tanggal 🍌
    @GET("api/v1/ibadah-sunnah")
    suspend fun getMyIbadahSunnah(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String
    ): Response<IbadahSunnahResponse>

    // Simpan daftar ibadah sunnah hari ini 🐒🔥
    @POST("api/v1/ibadah-sunnah")
    suspend fun saveIbadahSunnah(
        @Header("Authorization") token: String,
        @Body request: SaveIbadahSunnahRequest
    ): Response<Map<String, Any>>
}
