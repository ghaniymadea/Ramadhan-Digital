package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.Kegiatan
import com.pemula.ramadhandigital.model.KegiatanUser
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KegiatanUserController {
    private val services = Client.kegiatan

    /**
     * Ambil SEMUA daftar kegiatan master dari API 🍌
     * Digunakan untuk menampilkan list awal kegiatan yang tersedia.
     */
    suspend fun getAllKegiatan(): List<Kegiatan>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getAllKegiatan(token)
            if (response.isSuccessful) {
                // Mengambil field 'data' dari KegiatanResponse
                response.body()?.data
            } else {
                Log.e("KegiatanUserController", "Gagal ambil master kegiatan: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("KegiatanUserController", "Error getAll: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Ambil daftar kegiatan yang sudah diikuti oleh USER tertentu (logs/notes) 🍌🐒
     * Data ini yang masuk ke KegiatanUserAdapter.
     */
    suspend fun getKegiatanUser(idUser: Int): List<KegiatanUser>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getKegiatanByUser(token, idUser)
            if (response.isSuccessful) {
                // Buka bungkusan data dari KegiatanUserResponse
                response.body()?.data
            } else {
                Log.e("KegiatanUserController", "Gagal ambil data user: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("KegiatanUserController", "Error getByUser: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Fungsi untuk mendaftarkan atau menyimpan catatan kegiatan user ke API
     */
    suspend fun registerKegiatan(idUser: Int, idKegiatan: Int, note: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val request = KegiatanUser(
                id = 0, 
                idUser = idUser, 
                idKegiatan = idKegiatan, 
                note = note,
                user = null,
                kegiatan = null
            )
            val response = services.registerKegiatan(token, request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("KegiatanUserController", "Error simpan: ${e.localizedMessage}")
            false
        }
    }
}
