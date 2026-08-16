package com.pemula.ramadhandigital

import android.content.Context
import android.content.SharedPreferences
import com.pemula.ramadhandigital.model.Account

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("RamadhanDigitalSession", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ID = "user_id"
        private const val KEY_TOKEN = "token"
        private const val KEY_USERNAME = "username"
        private const val KEY_NAMA = "nama"
        private const val KEY_ROLE = "role"
        private const val KEY_KELAS = "kelas"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    /**
     * Simpan data login ke SharedPreferences 🍌🐒
     */
    fun saveSession(id: Int, token: String?, refreshToken: String?, username: String?, nama: String?, role: String?, kelas: String?) {
        val editor = prefs.edit()
        editor.putInt(KEY_ID, id)
        editor.putString(KEY_TOKEN, token)
        editor.putString(KEY_REFRESH_TOKEN, refreshToken)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_NAMA, nama)
        editor.putString(KEY_ROLE, role)
        editor.putString(KEY_KELAS, kelas)
        editor.apply()

        // Sync ke object Account global biar gampang dipake 🍌
        syncToAccount()
    }

    /**
     * Ambil data dari SharedPreferences dan masukkan ke object Account 🐒🔥
     */
    fun syncToAccount() {
        Account.Id = prefs.getInt(KEY_ID, 0)
        Account.Token = prefs.getString(KEY_TOKEN, null)
        Account.RefreshToken = prefs.getString(KEY_REFRESH_TOKEN, null)
        Account.Username = prefs.getString(KEY_USERNAME, null)
        Account.Nama = prefs.getString(KEY_NAMA, null)
        Account.Role = prefs.getString(KEY_ROLE, null)
        Account.Kelas = prefs.getString(KEY_KELAS, null)
    }

    /**
     * Cek apakah user sudah login (punya token) 🍌
     */
    fun isLoggedIn(): Boolean {
        return prefs.getString(KEY_TOKEN, null) != null
    }

    /**
     * Hapus semua data session (Logout) 🐒💨
     */
    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()

        // Kosongkan object Account global
        Account.Id = 0
        Account.Token = null
        Account.RefreshToken = null
        Account.Username = null
        Account.Nama = null
        Account.Role = null
        Account.Kelas = null
    }
}
