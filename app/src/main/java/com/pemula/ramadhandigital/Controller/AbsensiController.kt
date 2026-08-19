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
     * Mengambil data absensi/daftar siswa per kelas 🍌
     * Wajib pakai Token karena diproteksi Backend! 🔐
     */
    suspend fun getAbsensi(idKelas: String, tanggal: String): List<AbsensiItem>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getAbsensi(token, idKelas, tanggal)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("AbsensiController", "Gagal ambil absensi: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("AbsensiController", "Error: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Simpan absensi siswa 🐒🔥
     */
    suspend fun simpanAbsensi(request: PostAbsensiRequest): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.postAbsensi(token, request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("AbsensiController", "Error simpan absensi: ${e.localizedMessage}")
            false
        }
    }
}
