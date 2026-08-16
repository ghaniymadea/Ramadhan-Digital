package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.SetoranHafalan
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class SetoranHafalanController {
    private val services = Client.setoranHafalan

    /**
     * Ambil data setoran hafalan milik sendiri 🍌
     */
    suspend fun getSetoranByUser(idUser: Int): List<SetoranHafalan>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getSetoranByUser(token, idUser)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("SetoranHafalanController", "Gagal ambil data: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("SetoranHafalanController", "Error: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Simpan setoran hafalan baru 🐒🔥
     */
    suspend fun createSetoran(idSurah: Int, idStatus: Int, note: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val request = SetoranHafalan(
                id = 0,
                idUser = Account.Id,
                idSurah = idSurah,
                idBacaanSholat = null,
                idStatusSetoranHafalan = idStatus,
                note = note,
                tanggalSetoran = currentDate
            )

            val response = services.createSetoran(token, request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SetoranHafalanController", "Error simpan: ${e.localizedMessage}")
            false
        }
    }
}
