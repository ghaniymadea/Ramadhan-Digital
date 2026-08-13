package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class IbadahHarian(
    @SerializedName("id")
    val id: Int,
    
    // Sesuaikan dengan schema: id_user
    @SerializedName("id_user", alternate = ["idUser", "IdUser"])
    val idUser: Int,
    
    @SerializedName("tanggal")
    val tanggal: String?,
    
    // Sesuaikan dengan schema database PostgreSQL: membaca_alquran
    @SerializedName("membaca_alquran", alternate = ["membacaAlquran", "MembacaAlquran"])
    val membacaAlquran: Boolean = false,
    
    // Sesuaikan dengan schema database PostgreSQL: target_bacaan
    @SerializedName("target_bacaan", alternate = ["targetBacaan", "TargetBacaan"])
    val targetBacaan: String? = null
)

data class IbadahHarianResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<IbadahHarian>?
)
