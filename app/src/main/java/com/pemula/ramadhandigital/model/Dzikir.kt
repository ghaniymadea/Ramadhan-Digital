package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class Dzikir(
    @SerializedName("id", alternate = ["Id"]) val id: Int?,
    @SerializedName("nama", alternate = ["Nama", "title", "dzikir"]) val nama: String?,
    @SerializedName("arabic", alternate = ["Arabic", "teks", "ar"]) val arabic: String?,
    @SerializedName("terjemah", alternate = ["Terjemah", "translate", "translation", "idn"]) val terjemah: String?
)