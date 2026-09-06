package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

// Model utama untuk response GET absensi
data class AbsensiResponse(
    @SerializedName("status", alternate = ["Status", "state"]) val status: String? = null,
    @SerializedName("data", alternate = ["Data", "list", "List", "items", "results"]) val data: List<AbsensiItem>? = null
)

// Model detail per siswa
data class AbsensiItem(
    @SerializedName("id", alternate = ["Id", "id_absensi", "IdAbsensi"]) val id: Int = 0,
    
    @SerializedName("iduser", alternate = ["idUser", "IdUser", "id_user", "userId", "idsiswa", "IdSiswa"]) 
    val idUser: Int = 0,
    
    @SerializedName("namasiswa", alternate = ["namaSiswa", "NamaSiswa", "nama_siswa", "nama", "Nama", "nama_lengkap", "namaUser", "NamaUser", "nama_user", "nama_santri", "NamaSantri", "namalengkap"]) 
    val namaSiswa: String = "",
    
    @SerializedName("role", alternate = ["Role", "jabatan", "level", "jabatan_user"]) 
    val role: String? = null,
    
    @SerializedName("tanggal", alternate = ["Tanggal", "tgl", "date", "Date", "tanggal_absensi"]) 
    val tanggal: String? = null,
    
    @SerializedName("idstatusabsensi", alternate = ["idStatusAbsensi", "id_status_absensi", "IdStatusAbsensi", "idStatus", "IdStatus", "statusId"]) 
    var idStatusAbsensi: Int? = 0,

    @SerializedName("statusabsensi", alternate = ["statusAbsensi", "status_absensi", "StatusAbsensi", "status", "Status", "nama_status"]) 
    val statusAbsensi: String? = null,
    
    // Field Rekap (Dihitung di Client jika Backend Kosong) 📊
    var totalHadir: Int = 0,
    var totalIzin: Int = 0,
    var totalSakit: Int = 0,
    var totalAlpa: Int = 0
)

// DTO untuk POST data absensi
data class PostAbsensiRequest(
    @SerializedName("IdKelas") val idKelas: Int,
    @SerializedName("Tanggal") val tanggal: String,
    @SerializedName("SiswaList") val siswaList: List<PostAbsensiItem>
)

data class PostAbsensiItem(
    @SerializedName("IdUser") val idUser: Int,
    @SerializedName("IdStatusAbsensi") val idStatusAbsensi: Int
)

// Model response setelah simpan data
data class PostAbsensiResponse(
    @SerializedName("status", alternate = ["Status"]) val status: String? = null,
    @SerializedName("message", alternate = ["Message"]) val message: String? = null
)
