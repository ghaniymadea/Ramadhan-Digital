package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.Ayat
import com.pemula.ramadhandigital.model.Surah
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SurahController {
    private val services = Client.surah

    /**
     * Ambil daftar Surah (Juz Amma)
     * Response: List<Surah> langsung [...] 🍌🔥
     */
    suspend fun getJuzAmma(): List<Surah>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getSurah(token)
            if (response.isSuccessful) {
                // AMBIL BODY LANGSUNG! Tadi error karena pake .data padahal isinya List mentah 🍌
                response.body()?.filter { it.nomor >= 78 }
            } else {
                Log.e("SurahController", "Gagal ambil surah: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("SurahController", "Error: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Ambil daftar Ayat berdasarkan ID Surah
     * Response: List<Ayat> langsung [...] 🐒🚀
     */
    suspend fun getAyatBySurah(idSurah: Int): List<Ayat>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.getAyat(token, idSurah)
            if (response.isSuccessful) {
                // AMBIL BODY LANGSUNG! 🍌🔥
                response.body()
            } else {
                Log.e("SurahController", "Gagal ambil ayat: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("SurahController", "Error: ${e.localizedMessage}")
            null
        }
    }
}
