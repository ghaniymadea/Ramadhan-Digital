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
     * Mengambil data ibadah berdasarkan tanggal spesifik 📅
     * Memastikan Query terkirim dengan format yyyy-MM-dd
     */
    suspend fun getIbadahHarianByDate(tanggal: String?): IbadahHarian? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"

            // Jika tanggal null, biarkan null agar Retrofit tidak mengirim query sama sekali
            // Jika ada (misal: "2026-08-22T00:00:00"), bersihkan hanya jadi "2026-08-22"
            val cleanDate = tanggal?.let {
                if (it.contains("T")) it.split("T")[0] else it
            }

            Log.d("IbadahHarian", "Request URL Query: tanggal=$cleanDate")

            val response = services.getIbadahHarian(token, cleanDate)

            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("IbadahHarian", "Error ${response.code()}: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("IbadahHarian", "Exception: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Ambil data hari ini ☀️
     */
    suspend fun getIbadahHarianHariIni(): IbadahHarian? {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val currentDate = sdf.format(Date())
        return getIbadahHarianByDate(currentDate)
    }

    suspend fun getMonitoringKelas(idKelas: Int, tanggal: String): List<IbadahHarian>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val formattedDate = if (tanggal.contains("T")) tanggal.split("T")[0] else tanggal
            val response = services.getMonitoringKelas(token, idKelas, formattedDate)
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) { null }
    }

    suspend fun registerIbadahHarian(ibadah: IbadahHarian): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            ibadah.idUser = Account.Id
            val response = services.registerIbadahHarian(token, ibadah)
            response.isSuccessful
        } catch (e: Exception) { false }
    }
}
