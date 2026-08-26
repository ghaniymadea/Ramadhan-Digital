package com.pemula.ramadhandigital.controller

import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.SetoranHafalan
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SetoranHafalanController {
    private val services = Client.setoranHafalan

    // Digunakan Siswa & Guru untuk melihat data
    suspend fun getDaftarSetoran(): List<SetoranHafalan>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getAllSetoran(token)
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) { null }
    }

    // Digunakan Guru untuk monitoring per kelas
    suspend fun getSetoranKelas(idKelas: Int): List<SetoranHafalan>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getSetoranByKelas(token, idKelas)
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) { null }
    }

    suspend fun simpanSetoran(setoran: SetoranHafalan): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.createSetoran(token, setoran)
            response.isSuccessful
        } catch (e: Exception) { false }
    }

    suspend fun updateStatusSetoran(id: Int, setoran: SetoranHafalan): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.updateSetoran(token, id, setoran)
            response.isSuccessful
        } catch (e: Exception) { false }
    }
}