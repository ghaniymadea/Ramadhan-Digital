package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class IbadahHarianController {
    private val services = Client.ibadahHarian

    /**
     * Ambil data ibadah harian hari ini 🍌
     * Backend: GET /api/v1/ibadah-harian?tanggal=yyyy-MM-dd
     */
    suspend fun getIbadahHarianHariIni(): IbadahHarian? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = sdf.format(Date())
            
            val response = services.getIbadahHarian(token, currentDate)
            
            if (response.isSuccessful) {
                // Backend mengembalikan objek tunggal (Single Response) 🐒🔥
                response.body()?.data
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Simpan Ibadah Harian Baru ke Backend C# 🐒🔥
     * IdUser diambil otomatis dari token di backend
     */
    suspend fun registerIbadahHarian(target: String, membacaAlquran: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            
            val request = IbadahHarian(
                id = 0,
                idUser = Account.Id, 
                tanggal = sdf.format(Date()),
                membacaAlquran = membacaAlquran,
                targetBacaan = target
            )
            
            val response = services.registerIbadahHarian(token, request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
