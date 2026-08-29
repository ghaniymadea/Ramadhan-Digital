package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.pemula.ramadhandigital.databinding.ActivityOpeningBinding
import com.pemula.ramadhandigital.model.Account

class OpeningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpeningBinding
    private var isLogout = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivityOpeningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Deteksi apakah ini alur Login atau Logout
        isLogout = intent.getBooleanExtra("IS_LOGOUT", false)

        // Penyesuaian kamera 3D perspektif layar agar lebih "Deep"
        val distance = 15000 * resources.displayMetrics.density
        binding.pageAlquran.cameraDistance = distance

        binding.pageAlquran.post {
            jalankanAnimasiPageFlipPremium()
        }
    }

    private fun jalankanAnimasiPageFlipPremium() {
        val page = binding.pageAlquran
        page.visibility = View.VISIBLE
        page.pivotX = 0f // Engsel kiri
        page.pivotY = page.height / 2f

        if (isLogout) {
            // Efek Menutup Kitab (Logout)
            page.rotationY = -180f
            page.alpha = 0f
            page.scaleX = 0.8f
            page.scaleY = 0.8f
            
            page.animate()
                .rotationY(0f)
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1200L)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction { pindahHalaman() }
                .start()
        } else {
            // Efek Membuka Kitab (Login)
            page.rotationY = 0f
            page.alpha = 1f
            
            page.animate()
                .rotationY(-180f)
                .alpha(0f)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(1300L)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction { pindahHalaman() }
                .start()
        }
    }

    private fun pindahHalaman() {
        val intent = if (isLogout) {
            Intent(this, LoginActivity::class.java)
        } else {
            // Cek role dari Account yang sudah di-sync
            if (Account.isGuru()) {
                Intent(this, BerandaGuruActivity::class.java)
            } else {
                Intent(this, BerandaActivity::class.java)
            }
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
