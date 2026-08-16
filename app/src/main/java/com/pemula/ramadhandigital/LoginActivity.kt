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

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan Password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = Login(Username = username, Password = password)
            
            lifecycleScope.launch {
                val result: LoginRespons? = controller.loginController(data)
                
                if (result != null) {
                    // SIMPAN SESSION 🍌🐒
                    sessionManager.saveSession(
                        id = result.Id ?: 0,
                        token = result.Token,
                        refreshToken = result.RefreshToken,
                        username = result.Username,
                        nama = result.Nama,
                        role = result.Role,
                        kelas = result.Kelas
                    )
                    
                    Toast.makeText(this@LoginActivity, "Halo ${result.Nama}, selamat datang!", Toast.LENGTH_SHORT).show()
                    
                    if (result.Role == "1") {
                        startActivity(Intent(this@LoginActivity, BerandaGuruActivity::class.java))
                    } else {
                        startActivity(Intent(this@LoginActivity, BerandaActivity::class.java))
                    }
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Username atau Password salah", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}