package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class Ayat(
    @SerializedName("id", alternate = ["Id"]) 
    val id: Int?,
    
    // Sesuaikan dengan schema database: id_surah
    @SerializedName("id_surah", alternate = ["idSurah", "surah_id", "surahId"]) 
    val idSurah: Int?,
    
    @SerializedName("nomor", alternate = ["Nomor"]) 
    val nomor: Int?,
    
    @SerializedName("arab", alternate = ["Arab", "teks_arab"]) 
    val arab: String?,
    
    // Sesuaikan dengan schema database: terjemah
    @SerializedName("terjemah", alternate = ["terjemahan", "idn", "Terjemah"]) 
    val terjemah: String?
)
