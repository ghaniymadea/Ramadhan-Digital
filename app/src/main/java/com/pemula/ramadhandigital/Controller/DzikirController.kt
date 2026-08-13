package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Dzikir
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DzikirController {
    private val services = Client.dzikir

    suspend fun getDzikir(): List<Dzikir>? = withContext(Dispatchers.IO) {
        try {
            val response = services.getDzikir()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("DzikirController", "Gagal ambil data: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("DzikirController", "Error: ${e.localizedMessage}")
            null
        }
    }
}