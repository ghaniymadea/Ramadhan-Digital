package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class DetailSholatWajib(
    @SerializedName("id", alternate = ["Id"]) val id: Int = 0,
    @SerializedName("idIbadahHarian", alternate = ["IdIbadahHarian"]) val idIbadahHarian: Int = 0,
    @SerializedName("idKategoriSholatWajib", alternate = ["IdKategoriSholatWajib"]) val idKategoriSholatWajib: Int = 0,
    @SerializedName("kategori", alternate = ["Kategori"]) val kategori: String? = null,
    @SerializedName("idStatusSholatWajib", alternate = ["IdStatusSholatWajib"]) val idStatusSholatWajib: Int = 0,
    @SerializedName("status", alternate = ["Status"]) val status: String? = null
)

data class IbadahHarian(
    // Mendukung "id" (siswa) dan "idibadah" (guru monitoring)
    @SerializedName("id", alternate = ["Id", "idibadah"]) val id: Int = 0,
    
    // Mendukung "idUser" (siswa) dan "idsiswa" (guru monitoring)
    @SerializedName("idUser", alternate = ["IdUser", "idsiswa"]) var idUser: Int = 0,
    
    // Mendukung "namaUser" (siswa) dan "namalengkap" (guru monitoring) 🚀
    @SerializedName("namaUser", alternate = ["NamaUser", "namalengkap"]) val namaUser: String? = null,
    
    @SerializedName("tanggal", alternate = ["Tanggal"]) val tanggal: String? = null,
    
    @SerializedName("membacaAlquran", alternate = ["MembacaAlquran", "membacaalquran"]) val membacaAlquran: Boolean = false,
    
    @SerializedName("targetBacaan", alternate = ["TargetBacaan", "targetbacaan"]) val targetBacaan: String? = null,
    
    @SerializedName("sudahMengisi", alternate = ["sudahmengisi"]) val sudahMengisi: Boolean = false,
    
    @SerializedName("detailSholatWajibs", alternate = ["DetailSholatWajibs"]) val detailSholatWajibs: List<DetailSholatWajib>? = emptyList()
)

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
