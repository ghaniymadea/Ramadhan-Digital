package com.pemula.ramadhandigital.model

import com.google.gson.annotations.SerializedName

data class Kegiatan(
    @SerializedName("id", alternate = ["Id"])
    val id: Int,

    @SerializedName("judul", alternate = ["Judul", "nama", "Nama"])
    val judul: String?,

    @SerializedName("pemateri", alternate = ["Pemateri", "ustadz"])
    val pemateri: String?,

    @SerializedName("tanggal", alternate = ["Tanggal", "date"])
    val tanggal: String?,

    @SerializedName("kegiatanUsers", alternate = ["KegiatanUsers"])
    val kegiatanUsers: List<KegiatanUser>?,

    // Field tambahan yang mungkin masih digunakan di bagian lain aplikasi
    @SerializedName("jam", alternate = ["Jam", "waktu"])
    val jam: String? = null,

    @SerializedName("gambar", alternate = ["Gambar", "icon"])
    val gambar: Int? = null
)