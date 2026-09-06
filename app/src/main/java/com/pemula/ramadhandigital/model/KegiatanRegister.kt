package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class KegiatanRegister(
    @SerializedName("idUser", alternate = ["IdUser"]) val idUser: Int?,
    @SerializedName("idKegiatan", alternate = ["IdKegiatan"]) val idKegiatan: Int?,
    @SerializedName("note", alternate = ["Note"]) val note: String?
)

data class KegiatanRegisterResponse(
    @SerializedName("status", alternate = ["Status"]) val status: String?,
    @SerializedName("message", alternate = ["Message", "msg"]) val message: String?
)