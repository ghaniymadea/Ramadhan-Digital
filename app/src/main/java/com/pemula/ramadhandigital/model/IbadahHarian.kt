package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class IbadahHarian(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("id_user", alternate = ["idUser", "IdUser"])
    var idUser: Int,
    
    @SerializedName("tanggal")
    val tanggal: String?,
    
    @SerializedName("membaca_alquran", alternate = ["membacaAlquran", "MembacaAlquran"])
    val membacaAlquran: Boolean = false,
    
    @SerializedName("target_bacaan", alternate = ["targetBacaan", "TargetBacaan"])
    val targetBacaan: String? = null
)

// Bungkusan untuk ambil data tunggal (Sesuai backend: GET /api/v1/ibadah-harian) 🍌
data class SingleIbadahHarianResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: IbadahHarian?
)

// Bungkusan untuk ambil banyak (Jika diperlukan di masa depan)
data class IbadahHarianResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<IbadahHarian>?
)
