package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class SetoranHafalan(
    @SerializedName("id", alternate = ["Id"]) 
    val id: Int,
    
    @SerializedName("idUser", alternate = ["IdUser"]) 
    val idUser: Int,

    // TAMBAHKAN INI: Menangkap nama siswa langsung dari row database 🍌🐒
    @SerializedName("NamaSiswa", alternate = ["namaSiswa", "nama_lengkap"]) 
    val namaSiswa: String? = null,
    
    @SerializedName("idSurah", alternate = ["IdSurah"]) 
    val idSurah: Int,
    
    @SerializedName("idBacaanSholat", alternate = ["IdBacaanSholat"]) 
    val idBacaanSholat: Int?,
    
    @SerializedName("idStatusSetoranHafalan", alternate = ["IdStatusSetoranHafalan"]) 
    val idStatusSetoranHafalan: Int,
    
    @SerializedName("note", alternate = ["Note"]) 
    val note: String?,
    
    @SerializedName("tanggalSetoran", alternate = ["TanggalSetoran"]) 
    val tanggalSetoran: String?,
    
    @SerializedName("user", alternate = ["User"]) 
    val user: User? = null,
    
    @SerializedName("surah", alternate = ["Surah"]) 
    val surah: Surah? = null,
    
    @SerializedName("status", alternate = ["Status"]) 
    val status: StatusSetoran? = null
)

data class StatusSetoran(
    @SerializedName("id", alternate = ["Id"]) val id: Int,
    @SerializedName("nama", alternate = ["Nama"]) val nama: String
)

data class SetoranHafalanResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<SetoranHafalan>?
)

data class SingleSetoranResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: SetoranHafalan?
)

data class GenericResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?
)
