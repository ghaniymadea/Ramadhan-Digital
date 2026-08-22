package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.pemula.ramadhandigital.controller.AuthController
import com.pemula.ramadhandigital.databinding.ActivityLoginBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.Login
import com.pemula.ramadhandigital.model.LoginRespons
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {

        // ==========================================
        // PAKSA APLIKASI MENGGUNAKAN LIGHT MODE
        // ==========================================

        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)

        // ==========================================
        // VIEW BINDING
        // ==========================================

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ==========================================
        // SESSION MANAGER
        // ==========================================

        sessionManager = SessionManager(this)

        // ==========================================
        // AUTH CONTROLLER
        // ==========================================

        val controller = AuthController()

        // ==========================================
        // TOMBOL LOGIN
        // ==========================================

        binding.btnLogin.setOnClickListener {

            val username =
                binding.etUsername.text
                    .toString()
                    .trim()

            val password =
                binding.etPassword.text
                    .toString()
                    .trim()

            // ==========================================
            // VALIDASI USERNAME
            // ==========================================

            if (username.isEmpty()) {

                binding.etUsername.error =
                    "Username wajib diisi"

                binding.etUsername.requestFocus()

                return@setOnClickListener
            }

            // ==========================================
            // VALIDASI PASSWORD
            // ==========================================

            if (password.isEmpty()) {

                binding.etPassword.error =
                    "Password wajib diisi"

                binding.etPassword.requestFocus()

                return@setOnClickListener
            }

            // ==========================================
            // NONAKTIFKAN TOMBOL
            // AGAR TIDAK DOUBLE LOGIN
            // ==========================================

            binding.btnLogin.isEnabled = false

            // ==========================================
            // DATA LOGIN
            // ==========================================

            val data = Login(
                Username = username,
                Password = password
            )

            // ==========================================
            // PROSES LOGIN
            // ==========================================

            lifecycleScope.launch {

                try {

                    val result: LoginRespons? =
                        controller.loginController(data)

                    // ==================================
                    // LOGIN BERHASIL
                    // ==================================

                    if (result != null) {

                        // ==================================
                        // SIMPAN SESSION (Ditambah ID KELAS 🍌)
                        // ==================================

                        sessionManager.saveSession(
                            id = result.Id ?: 0,
                            token = result.Token,
                            refreshToken = result.RefreshToken,
                            username = result.Username,
                            nama = result.Nama,
                            role = result.Role,
                            kelas = result.Kelas,
                            idKelas = result.IdKelas
                        )

                        // ==================================
                        // SINKRONKAN SESSION KE ACCOUNT
                        // ==================================

                        sessionManager.syncToAccount()

                        // ==================================
                        // TENTUKAN ROLE
                        // ==================================

                        val tujuanActivity: Class<*> =
                            if (Account.isGuru()) {

                                BerandaGuruActivity::class.java

                            } else {

                                BerandaActivity::class.java
                            }

                        // ==================================
                        // PESAN SELAMAT DATANG
                        // ==================================

                        Toast.makeText(
                            this@LoginActivity,
                            "Halo ${result.Nama}, selamat datang!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // ==================================
                        // BUKA OPENING ACTIVITY
                        // ==================================

                        val intent = Intent(
                            this@LoginActivity,
                            OpeningActivity::class.java
                        )

                        // Kirim tujuan setelah animasi selesai
                        intent.putExtra(
                            "TUJUAN_ACTIVITY",
                            tujuanActivity.name
                        )

                        startActivity(intent)

                        // ==================================
                        // ANIMASI LOGIN → OPENING
                        // KANAN → KIRI
                        // ==================================

                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )

                        // ==================================
                        // TUTUP LOGIN
                        // ==================================

                        finish()

                    } else {

                        // ==================================
                        // LOGIN GAGAL
                        // ==================================

                        binding.btnLogin.isEnabled = true

                        Toast.makeText(
                            this@LoginActivity,
                            "Username atau Password salah",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {

                    // ==================================
                    // AKTIFKAN KEMBALI TOMBOL
                    // ==================================

                    binding.btnLogin.isEnabled = true

                    // ==================================
                    // ERROR
                    // ==================================

                    Toast.makeText(
                        this@LoginActivity,
                        "Terjadi kesalahan: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}