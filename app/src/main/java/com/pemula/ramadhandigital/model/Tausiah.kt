package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class Tausiah(
    @SerializedName("id", alternate = ["Id", "idTausiah"]) val id: Int = 0,
    @SerializedName("idUser", alternate = ["IdUser", "iduser", "userId", "id_user"]) var idUser: Int = 0,
    @SerializedName("tanggal", alternate = ["Tanggal", "date", "tgl", "Tgl"]) val tanggal: String? = null,
    @SerializedName("judulTausiah", alternate = ["JudulTausiah", "judul", "Judul", "namaTausiah"]) val judulTausiah: String?,
    @SerializedName("namaPenceramah", alternate = ["NamaPenceramah", "penceramah", "ustadz", "NamaUstadz", "penceramahTausiah"]) val namaPenceramah: String?,
    @SerializedName("ringkasan", alternate = ["Ringkasan", "note", "isi", "resume"]) val ringkasan: String?,
    
    @SerializedName("isSubmitted", alternate = ["IsSubmitted", "is_submitted", "status", "sudah_isi"]) val isSubmitted: Boolean = false
)

data class TausiahResponse(
    @SerializedName("status", alternate = ["Status"]) val status: String?,
    @SerializedName("data", alternate = ["Data", "list", "List", "items"]) val data: List<Tausiah>?
)