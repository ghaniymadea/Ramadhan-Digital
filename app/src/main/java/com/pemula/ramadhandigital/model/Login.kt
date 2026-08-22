package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("username") val Username: String,
    @SerializedName("password") val Password: String
)

data class LoginRespons (
    @SerializedName("id") val Id: Int?,
    @SerializedName("token") val Token: String?,
    @SerializedName("username") val Username: String?,
    @SerializedName("nama") val Nama: String?,
    @SerializedName("role") val Role:  String?,
    @SerializedName("kelas") val Kelas: String?,
    @SerializedName("idKelas") val IdKelas: Int?,
    @SerializedName("refreshToken") val RefreshToken: String?
)
