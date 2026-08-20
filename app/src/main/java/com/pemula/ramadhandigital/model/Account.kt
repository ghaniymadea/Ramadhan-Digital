package com.pemula.ramadhandigital.model

object Account {
    var Id: Int = 0
    var Token: String? = null
    var RefreshToken: String? = null
    var Username: String? = null
    var Nama: String? = null
    var Role: String? = null
    var Kelas: String? = null

    /**
     * ASISTEN PINTAR: Cek apakah user ini Pembimbing atau Siswa 🍌🐒
     * Bisa baca angka "1" atau tulisan "Pembimbing/Guru" dari Backend C#
     */
    fun isGuru(): Boolean {
        val r = Role?.trim() ?: ""
        return r == "1" || r.contains("Guru", true) || r.contains("Pembimbing", true)
    }
}
