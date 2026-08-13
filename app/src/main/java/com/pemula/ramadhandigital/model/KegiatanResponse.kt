package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class KegiatanResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<Kegiatan>?
)
data class KegiatanUserResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: List<KegiatanUser>?
)