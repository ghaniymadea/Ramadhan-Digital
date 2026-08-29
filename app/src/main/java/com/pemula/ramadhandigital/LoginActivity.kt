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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        val controller = AuthController()

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty()) {
                binding.etUsername.error = "Username wajib diisi"
                binding.etUsername.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPassword.error = "Password wajib diisi"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            binding.btnLogin.isEnabled = false
            val data = Login(Username = username, Password = password)

            lifecycleScope.launch {
                try {
                    val result: LoginRespons? = controller.loginController(data)

                    if (result != null) {
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
                        sessionManager.syncToAccount()

                        val tujuanActivity = if (Account.isGuru()) {
                            BerandaGuruActivity::class.java
                        } else {
                            BerandaActivity::class.java
                        }

                        Toast.makeText(this@LoginActivity, "Halo ${result.Nama}, selamat datang!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, tujuanActivity)
                        startActivity(intent)
                        
                        // ==========================================
                        // TRANSISI PREMIUM: ZOOM & FADE IN 🚀
                        // ==========================================
                        overridePendingTransition(R.anim.premium_enter, R.anim.premium_exit)
                        
                        finish()

                    } else {
                        binding.btnLogin.isEnabled = true
                        Toast.makeText(this@LoginActivity, "Username atau Password salah", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(this@LoginActivity, "Terjadi kesalahan: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}