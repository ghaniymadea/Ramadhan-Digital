package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class IbadahHarianController {
    private val services = Client.ibadahHarian

    /**
     * Mengambil data ibadah berdasarkan tanggal spesifik 📅
     * Memastikan Query terkirim dengan format yyyy-MM-dd
     */
    suspend fun getIbadahHarianByDate(tanggal: String?): IbadahHarian? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"

            val cleanDate = tanggal?.let {
                if (it.contains("T")) it.split("T")[0] else it
            }

            Log.d("IbadahHarian", "Request URL Query: tanggal=$cleanDate")

            val response = services.getIbadahHarian(token, cleanDate)

            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("IbadahHarian", "Error ${response.code()}: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("IbadahHarian", "Exception: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Guru: Mengambil data rekap harian 1 siswa pada tanggal tertentu 🕵️‍♂️
     */
    suspend fun getRekapSiswaSingleDate(idSiswa: Int, tanggal: String): IbadahHarian? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val cleanDate = if (tanggal.contains("T")) tanggal.split("T")[0] else tanggal
            
            // Gunakan API monitoring siswa dengan filter tanggal start & end yang sama
            val response = services.getRekapSiswa(token, idSiswa, cleanDate, cleanDate)
            
            if (response.isSuccessful) {
                // Ambil item pertama karena filternya spesifik 1 hari
                response.body()?.data?.firstOrNull()
            } else {
                Log.e("IbadahHarian", "Gagal rekap siswa [${response.code()}]")
                null
            }
        } catch (e: Exception) {
            Log.e("IbadahHarian", "Error rekap: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Ambil data hari ini ☀️
     */
    suspend fun getIbadahHarianHariIni(): IbadahHarian? {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val currentDate = sdf.format(Date())
        return getIbadahHarianByDate(currentDate)
    }

    suspend fun getMonitoringKelas(idKelas: Int, tanggal: String): List<IbadahHarian>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val formattedDate = if (tanggal.contains("T")) tanggal.split("T")[0] else tanggal
            val response = services.getMonitoringKelas(token, idKelas, formattedDate)
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) { null }
    }

    suspend fun registerIbadahHarian(ibadah: IbadahHarian): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            ibadah.idUser = Account.Id
            val response = services.registerIbadahHarian(token, ibadah)
            response.isSuccessful
        } catch (e: Exception) { false }
    }
}
