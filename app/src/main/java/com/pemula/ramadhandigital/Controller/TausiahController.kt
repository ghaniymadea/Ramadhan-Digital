package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.Tausiah
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TausiahController {
    private val services = Client.tausiah

    /**
     * Ambil semua data tausiah 🍌
     */
    suspend fun getAllTausiah(): List<Tausiah>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getAllTausiah(token)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("TausiahController", "Gagal ambil tausiah: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TausiahController", "Error: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Simpan tausiah baru sesuai backend C# 🐒🔥
     */
    suspend fun createTausiah(judul: String, penceramah: String, ringkasan: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val request = Tausiah(
                id = 0,
                idUser = Account.Id,
                tanggal = currentDate,
                judulTausiah = judul,
                namaPenceramah = penceramah,
                ringkasan = ringkasan
            )

            val response = services.createTausiah(token, request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("TausiahController", "Error simpan: ${e.localizedMessage}")
            false
        }
    }
}
