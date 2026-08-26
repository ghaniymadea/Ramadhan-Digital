package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class DetailSholatWajib(
    @SerializedName("id", alternate = ["Id"]) val id: Int = 0,
    @SerializedName("idIbadahHarian", alternate = ["IdIbadahHarian"]) val idIbadahHarian: Int = 0,
    @SerializedName("idKategoriSholatWajib", alternate = ["IdKategoriSholatWajib"]) val idKategoriSholatWajib: Int = 0,
    @SerializedName("kategori", alternate = ["Kategori"]) val kategori: String?,
    @SerializedName("idStatusSholatWajib", alternate = ["IdStatusSholatWajib"]) val idStatusSholatWajib: Int = 0,
    @SerializedName("status", alternate = ["Status"]) val status: String?
)

data class IbadahHarian(
    @SerializedName("id", alternate = ["Id"]) val id: Int = 0,
    @SerializedName("idUser", alternate = ["IdUser"]) var idUser: Int = 0,
    @SerializedName("namaUser", alternate = ["NamaUser"]) val namaUser: String? = null,
    @SerializedName("tanggal", alternate = ["Tanggal"]) val tanggal: String?, // yyyy-MM-dd
    @SerializedName("membacaAlquran", alternate = ["MembacaAlquran"]) val membacaAlquran: Boolean = false,
    @SerializedName("targetBacaan", alternate = ["TargetBacaan"]) val targetBacaan: String? = null,
    @SerializedName("detailSholatWajibs", alternate = ["DetailSholatWajibs"]) val detailSholatWajibs: List<DetailSholatWajib>? = emptyList()
)

// Response wrappers dengan support alternate case 🐒📊
data class SingleIbadahHarianResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: IbadahHarian?,
    @SerializedName("message") val message: String?
)

data class IbadahHarianResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<IbadahHarian>?,
    @SerializedName("message") val message: String?
)
