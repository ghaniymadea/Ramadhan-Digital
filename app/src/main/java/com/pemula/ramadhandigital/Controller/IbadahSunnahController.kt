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
     * Backend mengharapkan format dd-MM-yyyy (misal: 29-08-2026) 🚀
     */
    suspend fun getSunnahSiswa(idSiswa: Int, tanggal: String): List<IbadahSunnah>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            
            // Konversi yyyy-MM-dd -> dd-MM-yyyy agar sesuai dengan backend 🛠️
            val inputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputSdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            
            val cleanDate = if (tanggal.contains("T")) tanggal.split("T")[0] else tanggal
            val formattedDate = try {
                val date = inputSdf.parse(cleanDate)
                outputSdf.format(date!!)
            } catch (e: Exception) {
                cleanDate // fallback jika parsing gagal
            }
            
            Log.d("SunnahController", "Request monitoring ke: $formattedDate untuk ID: $idSiswa")
            
            val response = services.getMonitoringSunnahSiswa(token, idSiswa, formattedDate)
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
