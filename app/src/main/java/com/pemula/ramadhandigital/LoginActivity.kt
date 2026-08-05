package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.pemula.ramadhandigital.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        // Paksa aplikasi selalu mode terang
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if (username.isEmpty()) {
                binding.etUsername.error = "Username tidak boleh kosong"
                binding.etUsername.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPassword.error = "Password tidak boleh kosong"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            // Username dan password sementara
            if (username == "admin" && password == "123456") {

                Toast.makeText(
                    this,
                    "Login Berhasil",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(this, BerandaActivity::class.java)
                )

                finish()

            } else {

                Toast.makeText(
                    this,
                    "Username atau Password salah",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

        binding.tvForgot.setOnClickListener {

            Toast.makeText(
                this,
                "Fitur lupa password belum tersedia",
                Toast.LENGTH_SHORT
            ).show()

        }
    }
}