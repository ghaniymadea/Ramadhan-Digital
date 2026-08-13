package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class AbsensiResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<AbsensiItem>
)

data class AbsensiItem(
    @SerializedName("idUser") val idUser: Int,
    @SerializedName("namaSiswa") val namaSiswa: String,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("statusAbsensi") val statusAbsensi: String,
    @SerializedName("idStatusAbsensi") var idStatusAbsensi: Int
)

data class PostAbsensiRequest(
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("siswaList") val siswaList: List<PostAbsensiItem>
)

data class PostAbsensiItem(
    @SerializedName("idUser") val idUser: Int,
    @SerializedName("idStatusAbsensi") val idStatusAbsensi: Int
)

data class PostAbsensiResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)