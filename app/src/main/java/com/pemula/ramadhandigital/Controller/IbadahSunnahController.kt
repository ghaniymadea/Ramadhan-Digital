package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.IbadahSunnah
import com.pemula.ramadhandigital.model.SaveIbadahSunnahRequest
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class IbadahSunnahController {
    private val services = Client.ibadahSunnah

    /**
     * Ambil data Ibadah Sunnah milik sendiri hari ini 🍌
     */
    suspend fun getMyIbadahSunnahHariIni(): List<IbadahSunnah>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = sdf.format(Date())
            
            val response = services.getMyIbadahSunnah(token, currentDate)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("IbadahSunnahController", "Gagal ambil data: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("IbadahSunnahController", "Error: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Guru: Ambil data Sunnah untuk 1 siswa spesifik berdasarkan ID dan tanggal 🕵️‍♂️
     */
    suspend fun getSunnahSiswa(idSiswa: Int, tanggal: String): List<IbadahSunnah>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            // Pastikan format tanggal yyyy-MM-dd
            val cleanDate = if (tanggal.contains("T")) tanggal.split("T")[0] else tanggal
            
            val response = services.getMonitoringSunnahSiswa(token, idSiswa, cleanDate)
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) {
            Log.e("IbadahSunnahController", "Error getSunnahSiswa: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Simpan daftar Ibadah Sunnah 🐒🔥
     */
    suspend fun saveIbadahSunnah(idKategoriList: List<Int>): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = sdf.format(Date())
            
            val request = SaveIbadahSunnahRequest(
                tanggal = currentDate,
                idKategoriSunnahList = idKategoriList
            )
            
            val response = services.saveIbadahSunnah(token, request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("IbadahSunnahController", "Error simpan: ${e.localizedMessage}")
            false
        }
    }
}
