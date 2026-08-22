package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class OpeningActivity : AppCompatActivity() {

    private lateinit var bookOpeningView: BookOpeningView

    private var isGuru = false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        // ==========================================
        // FULL SCREEN
        // ==========================================

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        val controller =
            WindowInsetsControllerCompat(
                window,
                window.decorView
            )

        controller.hide(
            WindowInsetsCompat.Type.statusBars()
        )

        controller.hide(
            WindowInsetsCompat.Type.navigationBars()
        )

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat
                .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // ==========================================
        // LAYOUT
        // ==========================================

        setContentView(
            R.layout.activity_opening
        )

        // ==========================================
        // AMBIL ROLE
        // ==========================================

        isGuru =
            intent.getBooleanExtra(
                "IS_GURU",
                false
            )

        // ==========================================
        // VIEW KITAB
        // ==========================================

        bookOpeningView =
            findViewById(
                R.id.bookOpeningView
            )

        // ==========================================
        // NONAKTIFKAN BACK
        // ==========================================

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    // Tidak melakukan apa-apa
                }
            }
        )

        // ==========================================
        // MULAI ANIMASI
        // ==========================================

        bookOpeningView.postDelayed({

            bookOpeningView.startOpeningAnimation {

                masukKeBeranda()

            }

        }, 200)
    }


    private fun masukKeBeranda() {

        val intent: Intent

        if (isGuru) {

            intent =
                Intent(
                    this,
                    BerandaGuruActivity::class.java
                )

        } else {

            intent =
                Intent(
                    this,
                    BerandaActivity::class.java
                )
        }

        startActivity(intent)

        // ==========================================
        // SLIDE KANAN → KIRI
        // ==========================================

        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )

        finish()
    }
}