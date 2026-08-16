package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.pemula.ramadhandigital.databinding.ActivitySplashBinding
import com.pemula.ramadhandigital.model.Account

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Delay 3 detik (3000ms) 🍌🐒
        Handler(Looper.getMainLooper()).postDelayed({
            if (sessionManager.isLoggedIn()) {
                // Jika sudah login, langsung sinkronkan ke Account dan pindah ke Beranda 🚀
                sessionManager.syncToAccount()
                
                if (Account.Role == "1") {
                    startActivity(Intent(this, BerandaGuruActivity::class.java))
                } else {
                    startActivity(Intent(this, BerandaActivity::class.java))
                }
            } else {
                // Jika belum, ke LoginActivity 🍌
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 3000)
    }
}
