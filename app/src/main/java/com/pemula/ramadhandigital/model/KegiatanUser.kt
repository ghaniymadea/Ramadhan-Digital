package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class KegiatanUser(
    @SerializedName("id", alternate = ["Id", "id_user_kegiatan", "idKegiatanUser"]) val id: Int = 0,
    @SerializedName("idUser", alternate = ["IdUser", "iduser", "idsiswa", "IdSiswa", "userId"]) val idUser: Int = 0,
    @SerializedName("idKegiatan", alternate = ["IdKegiatan", "idkegiatan", "id_kegiatan"]) val idKegiatan: Int = 0,
    @SerializedName("note", alternate = ["Note", "catatan", "keterangan"]) val note: String? = null,
    
    // Support nested structure
    @SerializedName("user", alternate = ["User"]) val user: User? = null,
    @SerializedName("kegiatan", alternate = ["Kegiatan"]) val kegiatan: Kegiatan? = null
)