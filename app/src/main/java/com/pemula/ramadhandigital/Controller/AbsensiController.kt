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
    suspend fun getAbsensi(idKelas: Int, tanggal: String?): List<AbsensiItem>? = withContext(Dispatchers.IO) {
        try {
            val tokenRaw = Account.Token
            if (tokenRaw.isNullOrEmpty()) return@withContext null
            
            val token = "Bearer $tokenRaw"
            Log.d("AbsensiController", "Fetching Students: idKelas=$idKelas, tanggal=$tanggal")
            
            // Gunakan parameter opsional tanggal 🕵️‍♂️
            val tglParam = if (tanggal.isNullOrEmpty()) null else tanggal
            val response = services.getAbsensi(token, idKelas, tglParam)
            
            if (response.isSuccessful) {
                val data = response.body()?.data
                Log.d("AbsensiController", "Students Success: ${data?.size ?: 0} items")
                data
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

    /**
     * Mengambil Rekap Absensi per kelas 📋📊
     */
    suspend fun getRekapAbsensi(idKelas: Int, tanggal: String? = null): List<AbsensiItem>? = withContext(Dispatchers.IO) {
        try {
            val tokenRaw = Account.Token
            if (tokenRaw.isNullOrEmpty()) {
                Log.e("AbsensiController", "Token is empty!")
                return@withContext null
            }
            
            val token = "Bearer $tokenRaw"
            Log.d("AbsensiController", "Fetching Rekap: idKelas=$idKelas, tanggal=$tanggal")
            val response = services.getRekapKelas(token, idKelas, tanggal)
            if (response.isSuccessful) {
                val data = response.body()?.data
                Log.d("AbsensiController", "Rekap Success: ${data?.size ?: 0} items")
                data
            } else {
                Log.e("AbsensiController", "Gagal ambil rekap [${response.code()}]: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("AbsensiController", "Error getRekapAbsensi: ${e.localizedMessage}")
            null
        }
    }
}
