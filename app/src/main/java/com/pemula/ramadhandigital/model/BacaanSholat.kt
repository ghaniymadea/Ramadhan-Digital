package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class BacaanSholat(
    @SerializedName("id", alternate = ["Id"]) val id: Int?,
    @SerializedName("nama", alternate = ["Nama"]) val nama: String?,
    @SerializedName("arabic", alternate = ["Arabic", "teksArab"]) val arabic: String?,
    @SerializedName("translate", alternate = ["Translate", "terjemah"]) val translate: String?,
    @SerializedName("urutan", alternate = ["Urutan"]) val urutan: Int?
)