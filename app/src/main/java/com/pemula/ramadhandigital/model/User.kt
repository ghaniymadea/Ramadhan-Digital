package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id", alternate = ["Id"]) val id: Int,
    @SerializedName("username", alternate = ["Username"]) val username: String?,
    @SerializedName("nama", alternate = ["Nama"]) val nama: String?,
    @SerializedName("role", alternate = ["Role"]) val role: Int?,
    @SerializedName("kelas", alternate = ["Kelas"]) val kelas: String?
)