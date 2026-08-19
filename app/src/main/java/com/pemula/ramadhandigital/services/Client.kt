package com.pemula.ramadhandigital.services

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Client {

    private const val BASE_URL = "http://192.168.69.35:3000/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val http: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val auth: Authservices by lazy {
        http.create(Authservices::class.java)
    }

    val surah: Surahservices by lazy {
        http.create(Surahservices::class.java)
    }

    val bacaanSholat: BacaanSholatservices by lazy {
        http.create(BacaanSholatservices::class.java)
    }

    val dzikir: Dzikirservices by lazy {
        http.create(Dzikirservices::class.java)
    }

    val kegiatan: KegiatanUserservices by lazy {
        http.create(KegiatanUserservices::class.java)
    }
    
    val absensi: Absensiservices by lazy {
        http.create(Absensiservices::class.java)
    }

    val ibadahHarian: IbadahHarianservices by lazy {
        http.create(IbadahHarianservices::class.java)
    }

    val ibadahSunnah: IbadahSunnahservices by lazy {
        http.create(IbadahSunnahservices::class.java)
    }

    val tausiah: Tausiahservices by lazy {
        http.create(Tausiahservices::class.java)
    }

    val setoranHafalan: SetoranHafalanservices by lazy {
        http.create(SetoranHafalanservices::class.java)
    }
}
