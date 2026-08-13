package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.BacaanSholat
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BacaanSholatController {
    private val services = Client.bacaanSholat

    suspend fun getBacaanSholat(): List<BacaanSholat>? = withContext(Dispatchers.IO) {
        try {
            val response = services.getBacaanSholat()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("BacaanSholatController", "Gagal ambil data: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("BacaanSholatController", "Error: ${e.localizedMessage}")
            null
        }
    }
}