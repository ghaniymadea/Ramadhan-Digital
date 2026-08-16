package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class RegisterGuru(
    @SerializedName("idKelas") val idKelas: String, // String sesuai gambar Postman "2"
    @SerializedName("nama") val nama: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class RegisterGuruResponse(
    @SerializedName("message") val message: String?
)
