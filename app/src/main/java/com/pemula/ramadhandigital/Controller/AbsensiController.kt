package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.AbsensiItem
import com.pemula.ramadhandigital.model.PostAbsensiRequest
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AbsensiController {
    private val services = Client.absensi

    /**
     * Mengambil data absensi per kelas 🍌
     * idKelas di Backend C# bertipe Int
     */
    suspend fun getAbsensi(idKelas: Int, tanggal: String): List<AbsensiItem>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getAbsensi(token, idKelas, tanggal)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                val errorMsg = response.errorBody()?.string()
                Log.e("AbsensiController", "Gagal ambil absensi [${response.code()}]: $errorMsg")
                null
            }
        } catch (e: Exception) {
            Log.e("AbsensiController", "Error Exception: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Simpan absensi massal siswa oleh Guru 🐒🔥
     */
    suspend fun simpanAbsensi(request: PostAbsensiRequest): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.postAbsensi(token, request)
            if (!response.isSuccessful) {
                Log.e("AbsensiController", "Gagal simpan [${response.code()}]: ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("AbsensiController", "Error simpan absensi: ${e.localizedMessage}")
            false
        }
    }
}
