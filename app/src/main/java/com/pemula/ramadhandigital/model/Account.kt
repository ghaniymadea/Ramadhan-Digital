package com.pemula.ramadhandigital.model

import android.util.Base64
import org.json.JSONObject

object Account {
    var Id: Int = 0
    var Token: String? = null
    var RefreshToken: String? = null
    var Username: String? = null
    var Nama: String? = null
    var Role: String? = null
    var Kelas: String? = null
    var IdKelas: Int = 0

    /**
     * ASISTEN PINTAR: Cek apakah user ini Pembimbing atau Siswa 🍌🐒
     */
    fun isGuru(): Boolean {
        val r = Role?.trim() ?: ""
        return r == "1" || r.contains("Guru", true) || r.contains("Pembimbing", true)
    }

    /**
     * Ambil ID dari Token jika Id bernilai 0 🚀
     * Berguna jika state memory hilang tapi token masih ada di SharedPreferences
     */
    fun getUserIdFromToken(): Int {
        if (Id != 0) return Id
        
        val token = Token ?: return 0
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return 0
            
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)
            
            // Coba ambil dari claim umum JWT (id, sub, atau nameid)
            when {
                json.has("id") -> json.getInt("id")
                json.has("sub") -> json.getString("sub").toIntOrNull() ?: 0
                json.has("nameid") -> json.getString("nameid").toIntOrNull() ?: 0
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }
}
