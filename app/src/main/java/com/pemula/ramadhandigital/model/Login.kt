package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("Username") val Username: String,
    @SerializedName("Password") val Password: String
)

data class LoginRespons(
    @SerializedName("id", alternate = ["Id"]) val Id: Int?,
    @SerializedName("token", alternate = ["Token"]) val Token: String?,
    @SerializedName("refreshToken", alternate = ["RefreshToken"]) val RefreshToken: String?,
    @SerializedName("username", alternate = ["Username"]) val Username: String?,
    @SerializedName("nama", alternate = ["Nama"]) val Nama: String?,
    @SerializedName("role", alternate = ["Role"]) val Role: String?,
    @SerializedName("kelas", alternate = ["Kelas"]) val Kelas: String?,
    @SerializedName("idKelas", alternate = ["IdKelas", "id_kelas"]) val IdKelas: Int?
)
