package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class KegiatanRegister(
    @SerializedName("IdUser") val idUser: Int?,
    @SerializedName("IdKegiatan") val idKegiatan: Int?,
    @SerializedName("Note") val note: String?
)

data class KegiatanRegisterResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?
)