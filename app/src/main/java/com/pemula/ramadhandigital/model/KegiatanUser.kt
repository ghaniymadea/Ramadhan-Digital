package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class KegiatanUser(
    @SerializedName("id", alternate = ["Id"]) val id: Int,
    @SerializedName("idUser", alternate = ["IdUser"]) val idUser: Int,
    @SerializedName("idKegiatan", alternate = ["IdKegiatan"]) val idKegiatan: Int,
    @SerializedName("note", alternate = ["Note"]) val note: String?,
    
    // Monyet pasang kotak info User dan Kegiatan di sini ya! 🍌🐒
    @SerializedName("user", alternate = ["User"]) val user: User?,
    @SerializedName("kegiatan", alternate = ["Kegiatan"]) val kegiatan: Kegiatan?
)