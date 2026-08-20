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

        Handler(Looper.getMainLooper()).postDelayed({
            if (sessionManager.isLoggedIn()) {
                sessionManager.syncToAccount()
                
                // MONYET FIX: Pake isGuru() biar gak salah pintu! 🍌🚀
                if (Account.isGuru()) {
                    startActivity(Intent(this, BerandaGuruActivity::class.java))
                } else {
                    startActivity(Intent(this, BerandaActivity::class.java))
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 3000)
    }
}
