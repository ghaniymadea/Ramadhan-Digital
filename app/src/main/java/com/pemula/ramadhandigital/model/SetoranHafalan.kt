package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class SetoranHafalan(
    @SerializedName("id", alternate = ["Id", "id_setoran"]) 
    val id: Int,
    
    @SerializedName("idUser", alternate = ["IdUser", "id_user", "iduser"]) 
    val idUser: Int,

    @SerializedName("NamaSiswa", alternate = ["namaSiswa", "nama_lengkap", "nama_user"]) 
    val namaSiswa: String? = null,
    
    @SerializedName("idSurah", alternate = ["IdSurah", "id_surah"]) 
    val idSurah: Int,
    
    @SerializedName("idBacaanSholat", alternate = ["IdBacaanSholat", "id_bacaan_sholat", "idbacaan"]) 
    val idBacaanSholat: Int?,
    
    @SerializedName("idStatusSetoranHafalan", alternate = ["IdStatusSetoranHafalan", "id_status_setoran_hafalan", "idstatus", "idStatus"]) 
    val idStatusSetoranHafalan: Int,
    
    @SerializedName("note", alternate = ["Note", "catatan"]) 
    val note: String?,
    
    @SerializedName("tanggalSetoran", alternate = ["TanggalSetoran", "tanggal", "tgl"]) 
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
    // Jadikan nullable agar tidak crash saat backend kirim null 🛡️
    @SerializedName("nama", alternate = ["Nama", "nama_status"]) val nama: String? = null
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
