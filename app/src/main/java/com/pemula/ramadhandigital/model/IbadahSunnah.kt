package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class KategoriSunnah(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String
)

data class IbadahSunnah(
    @SerializedName("id") val id: Int,
    @SerializedName("idUser") val idUser: Int,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("idKategoriSunnah") val idKategoriSunnah: Int,
    @SerializedName("kategoriSunnah") val kategori: KategoriSunnah?
)

// DTO untuk POST sesuai backend C# 🍌
data class SaveIbadahSunnahRequest(
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("idKategoriSunnahList") val idKategoriSunnahList: List<Int>
)

data class IbadahSunnahResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<IbadahSunnah>?
)
