package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class DetailSholatWajib(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("idIbadahHarian") val idIbadahHarian: Int = 0,
    @SerializedName("idKategoriSholatWajib") val idKategoriSholatWajib: Int = 0,
    @SerializedName("kategori") val kategori: String?,
    @SerializedName("idStatusSholatWajib") val idStatusSholatWajib: Int = 0,
    @SerializedName("status") val status: String?
)

data class IbadahHarian(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("idUser") var idUser: Int = 0,
    @SerializedName("namaUser") val namaUser: String? = null,
    @SerializedName("tanggal") val tanggal: String?,
    @SerializedName("membacaAlquran") val membacaAlquran: Boolean = false,
    @SerializedName("targetBacaan") val targetBacaan: String? = null,
    @SerializedName("detailSholatWajibs") val detailSholatWajibs: List<DetailSholatWajib>? = emptyList()
)

// Bungkusan response tunggal sesuai Backend C# 🐒🔥
data class SingleIbadahHarianResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: IbadahHarian?,
    @SerializedName("message") val message: String?
)
