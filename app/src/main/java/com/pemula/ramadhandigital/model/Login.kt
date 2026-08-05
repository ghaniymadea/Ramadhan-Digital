package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName
data class Login(

    @SerializedName("username") val Username: String,
    @SerializedName("password") val Password: String
)

data class LoginRespons (
    @SerializedName("token") val Token: Int,
    @SerializedName("username") val Username: String,
    @SerializedName("nama") val Nama: String,
    @SerializedName("role") val Role: Int,
    @SerializedName("kelas") val Kelas: String,
    @SerializedName("refreshToken") val RefreshToken: Int
)