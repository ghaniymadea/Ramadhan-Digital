package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class Tausiah(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("idUser") var idUser: Int = 0,
    @SerializedName("tanggal") val tanggal: String? = null,
    @SerializedName("judulTausiah") val judulTausiah: String?,
    @SerializedName("namaPenceramah") val namaPenceramah: String?,
    @SerializedName("ringkasan") val ringkasan: String?,
    
    // Tambahkan properti ini biar gak error pas dipanggil di Activity! 🍌🐒
    @SerializedName("isSubmitted") val isSubmitted: Boolean = false
)

data class TausiahResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<Tausiah>?
)
