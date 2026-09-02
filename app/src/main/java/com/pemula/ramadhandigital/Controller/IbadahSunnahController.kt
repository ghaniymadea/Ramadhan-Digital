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
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
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
     * Backend Monitoring Sunnah MENGHARAPKAN format dd-MM-yyyy (misal: 02-09-2026) 🚀
     */
    suspend fun getSunnahSiswa(idSiswa: Int, tanggal: String): List<IbadahSunnah>? = withContext(Dispatchers.IO) {
        try {
            if (idSiswa == 0) return@withContext null
            val token = "Bearer ${Account.Token}"
            
            // Konversi yyyy-MM-dd -> dd-MM-yyyy sesuai spesifikasi backend monitoring sunnah 🛠️
            val inputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputSdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            
            val cleanDate = if (tanggal.contains("T")) tanggal.split("T")[0] else tanggal
            val formattedDate = try {
                val dateObj = inputSdf.parse(cleanDate)
                outputSdf.format(dateObj!!)
            } catch (e: Exception) {
                cleanDate // fallback
            }
            
            Log.d("SunnahController", "Request monitoring Sunnah ID: $idSiswa, Tgl: $formattedDate")
            
            val response = services.getMonitoringSunnahSiswa(token, idSiswa, formattedDate)
            if (response.isSuccessful) {
                val data = response.body()?.data
                Log.d("SunnahController", "Data amalan sunnah diterima: ${data?.size ?: 0} item")
                data
            } else {
                // Cobalah format ISO yyyy-MM-dd sebagai cadangan jika dd-MM-yyyy gagal 🔄
                val retryResponse = services.getMonitoringSunnahSiswa(token, idSiswa, cleanDate)
                if (retryResponse.isSuccessful) {
                    retryResponse.body()?.data
                } else {
                    Log.e("SunnahController", "Gagal fetch sunnah (Code: ${response.code()})")
                    null
                }
            }
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
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val currentDate = sdf.format(Date())
            
            val request = SaveIbadahSunnahRequest(
                tanggal = currentDate,
                idKategoriSunnahList = idKategoriList
            )
            
            val response = services.saveIbadahSunnah(token, request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("IbadahSunnahController", "Error simpan sunnah: ${e.localizedMessage}")
            false
        }
    }
}
