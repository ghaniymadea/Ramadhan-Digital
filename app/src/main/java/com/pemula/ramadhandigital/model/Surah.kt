package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class Surah(
    @SerializedName("id") val id: Int,
    @SerializedName("surah", alternate = ["surahName", "nama"]) val surahName: String?,
    @SerializedName("artisurat", alternate = ["artiSurat", "arti"]) val artiSurat: String?,
    @SerializedName("tempat_turun", alternate = ["tempatTurun", "type"]) val tempatTurun: String?,
    @SerializedName("nomor") val nomor: Int
)
