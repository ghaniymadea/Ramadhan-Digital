package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class KegiatanResponse(
    @SerializedName("status", alternate = ["Status"]) val status: String?,
    @SerializedName("data", alternate = ["Data", "list", "List"]) val data: List<Kegiatan>?
)

data class KegiatanUserResponse(
    @SerializedName("status", alternate = ["Status"]) val status: String?,
    @SerializedName("data", alternate = ["Data", "list", "List"]) val data: List<KegiatanUser>?
)