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
     * Siswa: Ambil data setoran hafalan milik sendiri 🍌
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
     * Guru: Ambil SEMUA data setoran siswa 🐒🔥
     */
    suspend fun getAllSetoran(): List<SetoranHafalan>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getAllSetoran(token)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                Log.e("SetoranHafalanController", "Gagal ambil semua data: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("SetoranHafalanController", "Error: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Guru: Update status setoran (Accept/Reject) 🍌🚀
     */
    suspend fun updateStatusSetoran(idSetoran: Int, idStatus: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val request = SetoranHafalan(
                id = idSetoran,
                idUser = 0,
                idSurah = 0,
                idBacaanSholat = null,
                idStatusSetoranHafalan = idStatus,
                note = null,
                tanggalSetoran = null
            )
            val response = services.updateSetoran(token, idSetoran, request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SetoranHafalanController", "Error update: ${e.localizedMessage}")
            false
        }
    }

    /**
     * Siswa/Guru: Simpan setoran hafalan baru 🐒🔥
     */
    suspend fun createSetoran(
        idUser: Int,
        idSurah: Int,
        idBacaan: Int?,
        idStatus: Int,
        note: String,
        tanggal: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val request = SetoranHafalan(
                id = 0,
                idUser = idUser,
                idSurah = idSurah,
                idBacaanSholat = idBacaan,
                idStatusSetoranHafalan = idStatus,
                note = note,
                tanggalSetoran = tanggal
            )

            val response = services.createSetoran(token, request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SetoranHafalanController", "Error simpan: ${e.localizedMessage}")
            false
        }
    }
}
