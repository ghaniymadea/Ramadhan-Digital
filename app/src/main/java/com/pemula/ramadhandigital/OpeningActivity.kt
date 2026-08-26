package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.pemula.ramadhandigital.databinding.ActivityOpeningBinding

class OpeningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpeningBinding
    private var tujuanActivity: String? = null
    private var reverseAnimation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivityOpeningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tujuanActivity = intent.getStringExtra("TUJUAN_ACTIVITY")
        reverseAnimation = intent.getBooleanExtra("REVERSE_ANIMATION", false)

        // Penyesuaian kamera 3D perspektif layar
        val distance = 10000 * resources.displayMetrics.density
        binding.pageAlquran.cameraDistance = distance

        binding.pageAlquran.post {
            jalankanAnimasiPageFlip()
        }
    }

    private fun jalankanAnimasiPageFlip() {
        val page = binding.pageAlquran
        page.visibility = View.VISIBLE

        // Penentuan Engsel (Pivot Point)
        page.pivotX = if (reverseAnimation) page.width.toFloat() else 0f
        page.pivotY = page.height / 2f

        // Initial State
        if (reverseAnimation) {
            page.rotationY = 180f
            page.alpha = 0.2f
        } else {
            page.rotationY = 0f
            page.alpha = 1f
        }

        val endRotation = if (reverseAnimation) 0f else -180f
        val endAlpha = if (reverseAnimation) 1f else 0.0f

        // Mengunci pergerakan ke GPU Hardware Layer
        page.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        page.animate()
            .rotationY(endRotation)
            .alpha(endAlpha)
            .setDuration(1100L) // Durasi ideal 1.1 detik
            .setInterpolator(FastOutSlowInInterpolator())
            .withEndAction {
                page.setLayerType(View.LAYER_TYPE_NONE, null)
                bukaTujuan()
            }
            .start()
    }

    private fun bukaTujuan() {
        val targetClass = when (tujuanActivity) {
            BerandaActivity::class.java.name -> BerandaActivity::class.java
            BerandaGuruActivity::class.java.name -> BerandaGuruActivity::class.java
            else -> LoginActivity::class.java
        }

        val intent = Intent(this, targetClass).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}