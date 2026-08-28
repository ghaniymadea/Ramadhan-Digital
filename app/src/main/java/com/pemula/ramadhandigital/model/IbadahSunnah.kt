package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class DetailIbadahSunnah(
    @SerializedName("id", alternate = ["id_detail"]) val id: Int = 0,
    @SerializedName("idIbadahSunnah", alternate = ["id_ibadah_sunnah"]) val idIbadahSunnah: Int = 0,
    @SerializedName("idKategoriIbadahSunnah", alternate = ["id_kategori_sunnah", "idKategoriSunnah", "idkategori"]) val idKategoriIbadahSunnah: Int = 0,
    @SerializedName("kategori", alternate = ["nama_kategori", "nama", "Kategori"]) val kategori: String?,
    @SerializedName("isDone", alternate = ["is_done", "isdone", "status"]) var isDone: Boolean = false
)

data class IbadahSunnah(
    @SerializedName("id", alternate = ["id_ibadah"]) val id: Int = 0,
    @SerializedName("idUser", alternate = ["id_user", "iduser", "idsiswa"]) var idUser: Int = 0,
    @SerializedName("tanggal", alternate = ["tgl", "Tanggal"]) val tanggal: String?,
    
    // Support nested structure
    @SerializedName("detailIbadahSunnahs", alternate = ["detail_ibadah_sunnah", "details"]) 
    val detailIbadahSunnahs: List<DetailIbadahSunnah>? = emptyList(),
    
    // Support flat structure (direct properties if returned as a list of amalan)
    @SerializedName("idKategoriIbadahSunnah", alternate = ["id_kategori_sunnah", "idKategoriSunnah"]) val flatIdKategori: Int = 0,
    @SerializedName("kategori", alternate = ["nama_kategori", "nama"]) val flatKategori: String? = null,
    @SerializedName("isDone", alternate = ["is_done", "status"]) val flatIsDone: Boolean = false
)

data class IbadahSunnahResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<IbadahSunnah>?,
    @SerializedName("message") val message: String?
)

data class SaveIbadahSunnahRequest(
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("idKategoriSunnahList") val idKategoriSunnahList: List<Int>
)
