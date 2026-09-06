package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class DetailSholatWajib(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("idIbadahHarian", alternate = ["id_ibadah_harian", "idibadahharian"]) val idIbadahHarian: Int = 0,
    @SerializedName("idKategoriSholatWajib", alternate = ["idkategorisholatwajib", "id_kategori", "idkategori"]) val idKategoriSholatWajib: Int = 0,
    @SerializedName("kategori", alternate = ["nama_kategori", "nama"]) val kategori: String? = null,
    @SerializedName("idStatusSholatWajib", alternate = ["idstatussholatwajib", "id_status", "idstatus"]) val idStatusSholatWajib: Int = 0,
    @SerializedName("status", alternate = ["nama_status"]) val status: String? = null
)

data class RingkasanHarian(
    @SerializedName("totalKategori", alternate = ["total_kategori", "totalkategori"]) val totalKategori: Int = 0,
    @SerializedName("sudahDilakukan", alternate = ["sudah_dilakukan", "sudahdilakukan", "sudahmengisi"]) val sudahDilakukan: Int = 0,
    @SerializedName("belumDilakukan", alternate = ["belum_dilakukan", "belumdilakukan"]) val belumDilakukan: Int = 0,
    @SerializedName("persentase", alternate = ["persen", "prosentase"]) val persentase: Int = 0
)

data class IbadahHarian(
    @SerializedName("id", alternate = ["id_ibadah", "idIbadah", "idibadah"]) val id: Int = 0,
    @SerializedName("idUser", alternate = ["id_user", "userId", "idsiswa", "iduser"]) var idUser: Int = 0,
    @SerializedName("namaUser", alternate = ["nama_user", "nama", "namalengkap", "namasiswa", "nama_lengkap"]) val namaUser: String? = null,
    @SerializedName("tanggal", alternate = ["tgl", "date", "tanggal_ibadah", "tanggalIbadah"]) val tanggal: String? = null,
    @SerializedName("membacaAlquran", alternate = ["membacaalquran", "is_quran", "isQuran"]) val membacaAlquran: Boolean = false,
    @SerializedName("targetBacaan", alternate = ["target_bacaan", "target", "targetbacaan", "target_quran"]) val targetBacaan: String? = null,
    @SerializedName("sudahMengisi", alternate = ["sudahmengisi", "is_filled", "isFilled", "is_filled_harian"]) val sudahMengisi: Boolean = false,
    
    @SerializedName("detailSholatWajibs", alternate = ["detailsholatwajibs", "detail_sholat_wajib", "details"]) 
    val detailSholatWajibs: List<DetailSholatWajib>? = emptyList()
)

data class SingleIbadahHarianResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: IbadahHarian?,
    @SerializedName("ringkasan") val ringkasan: RingkasanHarian? = null,
    @SerializedName("message") val message: String?
)

data class IbadahHarianResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<IbadahHarian>?,
    @SerializedName("ringkasan") val ringkasan: RingkasanHarian? = null,
    @SerializedName("message") val message: String?
)
