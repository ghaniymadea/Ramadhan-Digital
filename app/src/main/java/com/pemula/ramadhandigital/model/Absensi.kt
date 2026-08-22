package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

// Model utama untuk response GET absensi
data class AbsensiResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("data") val data: List<AbsensiItem>? = null
)

// Model detail per siswa
data class AbsensiItem(
    @SerializedName("iduser") val idUser: Int = 0,
    @SerializedName("namasiswa") val namaSiswa: String = "",
    @SerializedName("role") val role: String? = null,
    @SerializedName("tanggal") val tanggal: String? = null,
    @SerializedName("statusabsensi") val statusAbsensi: String? = null,
    @SerializedName("idstatusabsensi") var idStatusAbsensi: Int? = 0
)

// DTO untuk POST data absensi (Sesuaikan dengan PascalCase C# jika perlu)
data class PostAbsensiRequest(
    @SerializedName("Tanggal") val tanggal: String,
    @SerializedName("SiswaList") val siswaList: List<PostAbsensiItem>
)

data class PostAbsensiItem(
    @SerializedName("IdUser") val idUser: Int,
    @SerializedName("IdStatusAbsensi") val idStatusAbsensi: Int
)

// Model response setelah simpan data
data class PostAbsensiResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null
)
