package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.model.IbadahHarianResponse
import com.pemula.ramadhandigital.model.SingleIbadahHarianResponse
import retrofit2.Response
import retrofit2.http.*

interface IbadahHarianservices {

    // Gunakan "/" di akhir agar pas dengan group.MapGet("/") di C# 🍌
    @GET("api/v1/ibadah-harian/")
    suspend fun getIbadahHarian(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null
    ): Response<SingleIbadahHarianResponse>

    // Gunakan "/" di akhir agar pas dengan group.MapPost("/") di C# 🚀
    @POST("api/v1/ibadah-harian/")
    suspend fun registerIbadahHarian(
        @Header("Authorization") token: String,
        @Body request: IbadahHarian
    ): Response<Map<String, Any>>

    // Monitoring tetap menggunakan path yang sudah unik
    @GET("api/v1/ibadah-harian/monitoring/kelas/{idKelas}")
    suspend fun getMonitoringKelas(
        @Header("Authorization") token: String,
        @Path("idKelas") idKelas: Int,
        @Query("tanggal") tanggal: String? = null
    ): Response<IbadahHarianResponse>
}
