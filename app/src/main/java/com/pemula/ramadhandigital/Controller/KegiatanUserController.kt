package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.Kegiatan
import com.pemula.ramadhandigital.model.KegiatanRegister
import com.pemula.ramadhandigital.model.KegiatanUser
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KegiatanUserController {
    private val services = Client.kegiatan

    // GET /api/v1/kegiatan/
    suspend fun getAllKegiatan(): List<Kegiatan>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getAllKegiatan(token)
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) { null }
    }

    // GET /api/v1/kegiatan/user/{idUser}
    suspend fun getKegiatanUser(idUser: Int): List<KegiatanUser>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            // Gunakan ID dari param atau fallback ke token jika 0 🕵️‍♂️
            val targetId = if (idUser != 0) idUser else Account.getUserIdFromToken()
            
            val response = services.getKegiatanByUser(token, targetId)
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) { null }
    }

    // POST /api/v1/kegiatan/register
    suspend fun registerKegiatan(idUser: Int, idKegiatan: Int, note: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val targetId = if (idUser != 0) idUser else Account.getUserIdFromToken()
            
            // Gunakan DTO khusus Register agar key JSON (camelCase) pas dengan API 🚀🔥
            val request = KegiatanRegister(targetId, idKegiatan, note)
            val response = services.registerKegiatan(token, request)
            response.isSuccessful
        } catch (e: Exception) { false }
    }
}