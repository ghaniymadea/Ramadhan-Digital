package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class DetailSholatWajib(
    @SerializedName("id", alternate = ["Id", "id_detail", "idDetail"]) val id: Int = 0,
    @SerializedName("idIbadahHarian", alternate = ["IdIbadahHarian", "id_ibadah_harian", "idIbadah"]) val idIbadahHarian: Int = 0,
    @SerializedName("idKategoriSholatWajib", alternate = ["IdKategoriSholatWajib", "id_kategori_sholat_wajib", "idkategori", "idKategori", "idKategoriSholat"]) val idKategoriSholatWajib: Int = 0,
    @SerializedName("kategori", alternate = ["Kategori", "nama_kategori", "NamaKategori", "namaKategori", "nama"]) val kategori: String? = null,
    @SerializedName("idStatusSholatWajib", alternate = ["IdStatusSholatWajib", "id_status_sholat_wajib", "idstatus", "idStatus", "idStatusSholat"]) val idStatusSholatWajib: Int = 0,
    @SerializedName("status", alternate = ["Status", "nama_status", "NamaStatus", "namaStatus"]) val status: String? = null
)

data class IbadahHarian(
    @SerializedName("id", alternate = ["Id", "idibadah", "id_ibadah", "idIbadah"]) val id: Int = 0,
    @SerializedName("idUser", alternate = ["IdUser", "idsiswa", "id_siswa", "iduser", "id_user", "idSantri", "userId"]) var idUser: Int = 0,
    @SerializedName("namaUser", alternate = ["NamaUser", "namalengkap", "nama_user", "namasiswa", "NamaSiswa", "namaSantri"]) val namaUser: String? = null,
    @SerializedName("tanggal", alternate = ["Tanggal", "tgl", "Tgl", "tanggalIbadah"]) val tanggal: String? = null,
    @SerializedName("membacaAlquran", alternate = ["MembacaAlquran", "membacaalquran", "is_quran", "MembacaAlQuran", "isQuran"]) val membacaAlquran: Boolean = false,
    @SerializedName("targetBacaan", alternate = ["TargetBacaan", "targetbacaan", "target", "Target", "targetQuran"]) val targetBacaan: String? = null,
    @SerializedName("sudahMengisi", alternate = ["sudahmengisi", "SudahMengisi", "is_filled", "isFilled"]) val sudahMengisi: Boolean = false,
    
    // PEMETAAN LIST SHOLAT YANG SANGAT FLEKSIBEL 🚀
    @SerializedName("detailSholatWajibs", alternate = ["DetailSholatWajibs", "detail_sholat_wajib", "details", "Details", "detailSholat", "DetailSholat", "sholatDetails", "sholats", "Sholats"]) 
    val detailSholatWajibs: List<DetailSholatWajib>? = emptyList()
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
