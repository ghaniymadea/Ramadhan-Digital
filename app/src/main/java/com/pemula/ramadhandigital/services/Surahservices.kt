package com.pemula.ramadhandigital.services

import com.pemula.ramadhandigital.model.Ayat
import com.pemula.ramadhandigital.model.Surah
import retrofit2.Response
import retrofit2.http.*

interface Surahservices {

    // Ambil Surah: Format List langsung [...] agar muncul lagi! 🍌
    @GET("api/v1/quran/surah")
    suspend fun getSurah(
        @Header("Authorization") token: String
    ): Response<List<Surah>>

    // Ambil Ayat: Format List langsung [...] agar sinkron dengan backend! 🐒🔥
    @GET("api/v1/quran/ayat/surah/{idSurah}")
    suspend fun getAyat(
        @Header("Authorization") token: String,
        @Path("idSurah") idSurah: Int
    ): Response<List<Ayat>>
}
