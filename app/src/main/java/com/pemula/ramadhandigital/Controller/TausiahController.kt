package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.Tausiah
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TausiahController {
    private val services = Client.tausiah

    /**
     * Ambil semua data tausiah dari server 🍌
     */
    suspend fun getAllTausiah(): List<Tausiah>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getAllTausiah(token)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("TausiahController", "Gagal ambil data: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TausiahController", "Error: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Simpan Tausiah Baru ke Backend C# 🐒🔥
     * Monyet ganti nama fungsi jadi saveTausiah biar sinkron sama Activity! 🍌
     */
    suspend fun saveTausiah(tausiah: Tausiah): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            // Backend C# menggunakan POST /api/v1/tausiah
            val response = services.createTausiah(token, tausiah)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("TausiahController", "Error simpan: ${e.localizedMessage}")
            false
        }
    }
}
