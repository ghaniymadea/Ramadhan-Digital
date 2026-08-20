package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class IbadahHarian(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("idUser") var idUser: Int = 0,
    @SerializedName("tanggal") val tanggal: String?,
    @SerializedName("membacaAlquran") val membacaAlquran: Boolean = false,
    @SerializedName("targetBacaan") val targetBacaan: String? = null
)

// Bungkusan response tunggal sesuai Backend C# 🐒🔥
data class SingleIbadahHarianResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: IbadahHarian?,
    @SerializedName("message") val message: String?
)
