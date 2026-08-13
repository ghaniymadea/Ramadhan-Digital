package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class SurahResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<Surah>?
)

data class AyatResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<Ayat>?
)