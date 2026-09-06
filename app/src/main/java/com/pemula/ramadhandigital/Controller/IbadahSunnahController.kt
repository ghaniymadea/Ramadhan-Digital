package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.IbadahSunnah
import com.pemula.ramadhandigital.model.SaveIbadahSunnahRequest
import com.pemula.ramadhandigital.services.Client
import com.pemula.ramadhandigital.utils.DateHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class IbadahSunnahController {
    private val services = Client.ibadahSunnah

    /**
     * Ambil data Ibadah Sunnah milik sendiri hari ini 🍌
     */
    suspend fun getMyIbadahSunnahHariIni(): List<IbadahSunnah>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val currentDate = DateHelper.getTodayApi()
            
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
            
            // Backend expects yyyy-MM-dd for DateOnly 🚀
            val cleanDate = DateHelper.stripTime(tanggal) ?: DateHelper.getTodayApi()
            
            Log.d("SunnahController", "Request monitoring Sunnah ID: $idSiswa, Tgl: $cleanDate")
            
            val response = services.getMonitoringSunnahSiswa(token, idSiswa, cleanDate)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("SunnahController", "Gagal fetch sunnah (Code: ${response.code()})")
                null
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
            val tokenRaw = Account.Token
            if (tokenRaw.isNullOrEmpty()) return@withContext false
            
            val token = "Bearer $tokenRaw"
            // Backend expects yyyy-MM-dd for DateOnly 🚀
            val currentDate = DateHelper.getTodayApi()
            
            val request = SaveIbadahSunnahRequest(
                tanggal = currentDate,
                idKategoriSunnahList = idKategoriList
            )
            
            Log.d("IbadahSunnah", "Saving Sunnah: Tanggal=$currentDate, Count=${idKategoriList.size}")
            val response = services.saveIbadahSunnah(token, request)
            if (!response.isSuccessful) {
                Log.e("IbadahSunnah", "Failed: ${response.code()} - ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("IbadahSunnahController", "Error simpan sunnah: ${e.localizedMessage}")
            false
        }
    }
}
