package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class AbsensiResponse(
    @SerializedName("status", alternate = ["Status"]) val status: String,
    @SerializedName("data", alternate = ["Data"]) val data: List<AbsensiItem>?
)

data class AbsensiItem(
    @SerializedName("IdUser", alternate = ["idUser"]) val idUser: Int,
    @SerializedName("NamaSiswa", alternate = ["namaSiswa"]) val namaSiswa: String,
    @SerializedName("Tanggal", alternate = ["tanggal"]) val tanggal: String?,
    @SerializedName("StatusAbsensi", alternate = ["statusAbsensi"]) val statusAbsensi: String?,
    @SerializedName("IdStatusAbsensi", alternate = ["idStatusAbsensi"]) var idStatusAbsensi: Int? = 0
)

data class PostAbsensiRequest(
    @SerializedName("Tanggal") val tanggal: String,
    @SerializedName("SiswaList") val siswaList: List<PostAbsensiItem>
)

data class PostAbsensiItem(
    @SerializedName("IdUser") val idUser: Int,
    @SerializedName("IdStatusAbsensi") val idStatusAbsensi: Int
)

data class PostAbsensiResponse(
    @SerializedName("status", alternate = ["Status"]) val status: String,
    @SerializedName("message", alternate = ["Message", "message"]) val message: String?
)
