package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("idRole") val idRole: Int,
    @SerializedName("idKelas") val idKelas: Int,
    @SerializedName("nama") val nama: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class CommonResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("token") val token: String? = null
)
