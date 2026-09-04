package com.pemula.ramadhandigital.controller

import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.SetoranHafalan
import com.pemula.ramadhandigital.model.User
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SetoranHafalanController {
    private val services = Client.setoranHafalan

    // Digunakan Guru untuk melihat semua data setoran berdasarkan tipe 📋
    suspend fun getDaftarSetoran(type: String): List<SetoranHafalan>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = if (type == "SURAH") {
                services.getAllSetoranSurah(token)
            } else {
                services.getAllSetoranBacaanSholat(token)
            }
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) { null }
    }

    // Digunakan Siswa untuk melihat riwayat setoran miliknya sendiri berdasarkan tipe 👦🚀
    suspend fun getSetoranSiswa(idUser: Int, type: String): List<SetoranHafalan>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = if (type == "SURAH") {
                services.getSetoranSurahByUserId(token, idUser)
            } else {
                services.getSetoranBacaanSholatByUserId(token, idUser)
            }
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) { null }
    }

    // Digunakan Guru untuk monitoring per kelas
    suspend fun getSetoranKelas(idKelas: Int, type: String = "SURAH"): List<SetoranHafalan>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = if (type == "SURAH") services.getAllSetoranSurah(token) else services.getAllSetoranBacaanSholat(token)
            
            if (response.isSuccessful) {
                response.body()?.data?.filter { it.user?.idKelas == idKelas }
            } else null
        } catch (e: Exception) { null }
    }

    suspend fun simpanSetoran(setoran: SetoranHafalan, type: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = if (type == "SURAH") {
                services.createSetoranSurah(token, setoran)
            } else {
                services.createSetoranBacaanSholat(token, setoran)
            }
            response.isSuccessful
        } catch (e: Exception) { false }
    }

    suspend fun updateStatusSetoran(id: Int, setoran: SetoranHafalan, type: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = if (type == "SURAH") {
                services.updateSetoranSurah(token, id, setoran)
            } else {
                services.updateSetoranBacaanSholat(token, id, setoran)
            }
            response.isSuccessful
        } catch (e: Exception) { false }
    }

    suspend fun deleteSetoran(id: Int, type: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = if (type == "SURAH") {
                services.deleteSetoranSurah(token, id)
            } else {
                services.deleteSetoranBacaanSholat(token, id)
            }
            response.isSuccessful
        } catch (e: Exception) { false }
    }
}