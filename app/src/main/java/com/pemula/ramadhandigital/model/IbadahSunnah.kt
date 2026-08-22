package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class DetailIbadahSunnah(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("idIbadahSunnah") val idIbadahSunnah: Int = 0,
    @SerializedName("idKategoriIbadahSunnah") val idKategoriIbadahSunnah: Int = 0,
    @SerializedName("kategori") val kategori: String?,
    @SerializedName("isDone") var isDone: Boolean = false
)

data class IbadahSunnah(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("idUser") var idUser: Int = 0,
    @SerializedName("tanggal") val tanggal: String?,
    @SerializedName("detailIbadahSunnahs") val detailIbadahSunnahs: List<DetailIbadahSunnah>? = emptyList()
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
