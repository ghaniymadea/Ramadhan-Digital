package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class Tausiah(
    @SerializedName("id") val id: Int,
    @SerializedName("idUser") var idUser: Int,
    @SerializedName("tanggal") val tanggal: String?,
    @SerializedName("judulTausiah") val judulTausiah: String?,
    @SerializedName("namaPenceramah") val namaPenceramah: String?,
    @SerializedName("ringkasan") val ringkasan: String?
)

data class TausiahResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<Tausiah>?
)

data class SingleTausiahResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: Tausiah?
)
