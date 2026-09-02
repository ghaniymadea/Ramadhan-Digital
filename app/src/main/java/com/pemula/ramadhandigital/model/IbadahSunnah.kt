package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class DetailIbadahSunnah(
    @SerializedName("id", alternate = ["id_detail"]) val id: Int = 0,
    @SerializedName("idIbadahSunnah") val idIbadahSunnah: Int = 0,
    @SerializedName("idKategoriSunnah", alternate = ["idKategoriIbadahSunnah", "id_kategori_sunnah"]) val idKategoriSunnah: Int = 0,
    @SerializedName("nama", alternate = ["kategori", "nama_kategori"]) val nama: String?,
    @SerializedName("sudahDilakukan", alternate = ["isDone", "is_done", "status"]) var sudahDilakukan: Boolean = false
)

data class RingkasanSunnah(
    @SerializedName("totalKategori") val totalKategori: Int = 0,
    @SerializedName("sudahDilakukan") val sudahDilakukan: Int = 0,
    @SerializedName("belumDilakukan") val belumDilakukan: Int = 0,
    @SerializedName("persentase") val persentase: Int = 0
)

data class IbadahSunnah(
    @SerializedName("idIbadahSunnah", alternate = ["id", "id_ibadah"]) val id: Int = 0,
    @SerializedName("idUser", alternate = ["id_user", "iduser", "idsiswa"]) var idUser: Int = 0,
    @SerializedName("tanggal", alternate = ["tgl", "Tanggal"]) val tanggal: String?,
    
    // Field Monitoring (Sesuai JSON Monitoring Santri terbaru) 🚀
    @SerializedName("idKategoriSunnah") val idKategoriSunnah: Int = 0,
    @SerializedName("nama", alternate = ["kategori", "nama_kategori"]) val nama: String? = null,
    @SerializedName("sudahDilakukan", alternate = ["isDone", "is_done", "status"]) val sudahDilakukan: Boolean = false,

    // Support nested structure (jika ada)
    @SerializedName("detailIbadahSunnahs", alternate = ["detail_ibadah_sunnah", "details", "Details", "detailSunnah"]) 
    val detailIbadahSunnahs: List<DetailIbadahSunnah>? = emptyList()
)

data class IbadahSunnahResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("idSiswa") val idSiswa: Int? = 0,
    @SerializedName("tanggal") val tanggal: String? = null,
    @SerializedName("ringkasan") val ringkasan: RingkasanSunnah? = null,
    @SerializedName("data") val data: List<IbadahSunnah>?,
    @SerializedName("message") val message: String?
)

data class SaveIbadahSunnahRequest(
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("idKategoriSunnahList") val idKategoriSunnahList: List<Int>
)
