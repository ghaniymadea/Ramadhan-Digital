package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class SetoranHafalan(
    @SerializedName("id") val id: Int,
    @SerializedName("idUser") val idUser: Int,
    @SerializedName("idSurah") val idSurah: Int,
    @SerializedName("idBacaanSholat") val idBacaanSholat: Int?,
    @SerializedName("idStatusSetoranHafalan") val idStatusSetoranHafalan: Int,
    @SerializedName("note") val note: String?,
    @SerializedName("tanggalSetoran") val tanggalSetoran: String?,
    
    // Relasi tambahan jika dikirim backend
    @SerializedName("surah") val surah: Surah? = null,
    @SerializedName("status") val status: StatusSetoran? = null
)

data class StatusSetoran(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String
)

data class SetoranHafalanResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<SetoranHafalan>?
)
