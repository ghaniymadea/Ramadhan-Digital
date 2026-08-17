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

        // Delay 3 detik biar gaya dikit 🍌🐒
        Handler(Looper.getMainLooper()).postDelayed({
            if (sessionManager.isLoggedIn()) {
                // SINKRONKAN DATA DARI MEMORI HP KE APLIKASI
                sessionManager.syncToAccount()
                
                // ARAHKAN KE PINTU YANG BENAR SESUAI ROLE! 🚀
                // Role "1" = Pembimbing/Guru
                if (Account.Role == "1") {
                    val intent = Intent(this, BerandaGuruActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, BerandaActivity::class.java)
                    startActivity(intent)
                }
            } else {
                // Belum login, ke halaman Login 🍌
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 3000)
    }
}
