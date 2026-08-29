package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.model.IbadahHarianResponse
import com.pemula.ramadhandigital.model.SingleIbadahHarianResponse
import retrofit2.Response
import retrofit2.http.*

interface IbadahHarianservices {

    // 1. Simpan / Ambil Ibadah Hari Ini (Siswa) 🚀
    @GET("api/v1/ibadah-harian")
    suspend fun getIbadahHarian(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null
    ): Response<SingleIbadahHarianResponse>

    @POST("api/v1/ibadah-harian")
    suspend fun registerIbadahHarian(
        @Header("Authorization") token: String,
        @Body request: IbadahHarian
    ): Response<Map<String, Any>>

    // 2. Riwayat Ibadah (Siswa) 📅
    @GET("api/v1/ibadah-harian/riwayat")
    suspend fun getRiwayatSiswa(
        @Header("Authorization") token: String,
        @Query("startDate") start: String? = null,
        @Query("endDate") end: String? = null
    ): Response<IbadahHarianResponse>

    // 3. Monitoring Kelas (Guru) 👨‍🏫
    @GET("api/v1/ibadah-harian/monitoring/kelas/{idKelas}")
    suspend fun getMonitoringKelas(
        @Header("Authorization") token: String,
        @Path("idKelas") idKelas: Int,
        @Query("tanggal") tanggal: String? = null
    ): Response<IbadahHarianResponse>

    // 4. Rekap Riwayat 1 Siswa (Guru) 🕵️‍♂️
    // PERBAIKAN: Menggunakan rute /monitoring/siswa/{idSiswa} sesuai spesifikasi backend 🚀
    // Path parameter disamakan menjadi idSiswa
    @GET("api/v1/ibadah-harian/monitoring/siswa/{idSiswa}")
    suspend fun getRekapSiswa(
        @Header("Authorization") token: String,
        @Path("idSiswa") idSiswa: Int,
        @Query("startDate") start: String? = null,
        @Query("endDate") end: String? = null
    ): Response<IbadahHarianResponse>
}
