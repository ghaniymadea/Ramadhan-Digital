package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class DetailIbadahSunnah(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("idIbadahSunnah", alternate = ["idibadahsunnah", "id_detail_sunnah"]) val idIbadahSunnah: Int = 0,
    @SerializedName("idKategoriSunnah", alternate = ["idkategorisunnah", "id_kat"]) val idKategoriSunnah: Int = 0,
    @SerializedName("nama", alternate = ["nama_kategori"]) val nama: String?,
    @SerializedName("sudahDilakukan", alternate = ["is_done", "isDone", "status"]) var sudahDilakukan: Boolean = false
)

data class RingkasanSunnah(
    @SerializedName("totalKategori", alternate = ["totalkategori"]) val totalKategori: Int = 0,
    @SerializedName("sudahDilakukan", alternate = ["sudahmengisi"]) val sudahDilakukan: Int = 0,
    @SerializedName("belumDilakukan", alternate = ["belumdilakukan"]) val belumDilakukan: Int = 0,
    @SerializedName("persentase", alternate = ["prosentase", "persen"]) val persentase: Int = 0
)

data class IbadahSunnah(
    @SerializedName("id", alternate = ["id_ibadah"]) val id: Int = 0,
    @SerializedName("idUser", alternate = ["iduser", "idsiswa"]) var idUser: Int = 0,
    @SerializedName("tanggal", alternate = ["tgl", "date"]) val tanggal: String?,
    
    @SerializedName("idKategoriSunnah", alternate = ["idkategorisunnah"]) val idKategoriSunnah: Int = 0,
    @SerializedName("nama", alternate = ["nama_sunnah", "nama_kategori"]) val nama: String? = null,
    @SerializedName("sudahDilakukan", alternate = ["sudahmengisi", "is_done", "isDone", "status"]) val sudahDilakukan: Boolean = false,
    @SerializedName("idIbadahSunnah", alternate = ["IdIbadahSunnah", "idibadahsunnah"]) val idIbadahSunnah: Int = 0,

    @SerializedName("detailIbadahSunnahs", alternate = ["details", "detailSunnah"]) 
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
    @SerializedName("Tanggal") val tanggal: String,
    @SerializedName("IdKategoriSunnahList") val idKategoriSunnahList: List<Int>
)
