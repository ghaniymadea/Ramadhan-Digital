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
     * Ambil data Ibadah Harian user untuk hari ini 🍌
     * Sesuai Backend: GET /api/v1/ibadah-harian?tanggal=yyyy-MM-dd
     */
    suspend fun getIbadahHarianHariIni(): List<IbadahHarian>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = sdf.format(Date())
            
            val response = services.getIbadahHarian(token, currentDate)
            
            if (response.isSuccessful) {
                // Jika sukses, masukkan data tunggal ke dalam List agar bisa dibaca Adapter 🐒
                val data = response.body()?.data
                if (data != null) listOf(data) else emptyList()
            } else if (response.code() == 404) {
                // Jika 404 (Data tidak ditemukan), kembalikan list kosong biar gak error 🍌
                emptyList()
            } else {
                Log.e("IbadahHarianController", "Gagal: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("IbadahHarianController", "Error: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Simpan Ibadah Harian baru 🐒🔥
     */
    suspend fun registerIbadahHarian(target: String, membacaAlquran: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = sdf.format(Date())
            
            val request = IbadahHarian(
                id = 0,
                idUser = Account.Id, 
                tanggal = currentDate,
                membacaAlquran = membacaAlquran,
                targetBacaan = target
            )
            
            val response = services.registerIbadahHarian(token, request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("IbadahHarianController", "Error simpan: ${e.localizedMessage}")
            false
        }
    }
}
