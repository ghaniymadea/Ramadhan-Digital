package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IbadahHarianController {
    private val services = Client.ibadahHarian

    /**
     * Mengambil log Ibadah Harian user
     */
    suspend fun getIbadahHarian(idUser: Int): List<IbadahHarian>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getIbadahHarian(token, idUser)
            
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("IbadahHarianController", "Gagal ambil data: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("IbadahHarianController", "Error: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Menyimpan Ibadah Harian baru
     */
    suspend fun registerIbadahHarian(target: String, membacaAlquran: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val currentDate = sdf.format(java.util.Date())
            
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
