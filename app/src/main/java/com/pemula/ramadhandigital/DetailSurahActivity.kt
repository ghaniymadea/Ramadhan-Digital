package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.AyatAdapter
import com.pemula.ramadhandigital.controller.SurahController
import com.pemula.ramadhandigital.databinding.ActivityDetailSurahBinding
import kotlinx.coroutines.launch

class DetailSurahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSurahBinding

    private val controller = SurahController()

    override fun onCreate(savedInstanceState: Bundle?) {

        // =====================================================
        // PAKSA SELALU MODE TERANG
        // =====================================================

        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)

        // =====================================================
        // VIEW BINDING
        // =====================================================

        binding = ActivityDetailSurahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // =====================================================
        // AMBIL DATA DARI JuzAmmaActivity
        // =====================================================

        val idSurah = intent.getIntExtra(
            "ID_SURAH",
            0
        )

        val namaSurah = intent.getStringExtra(
            "NAMA_SURAH"
        ) ?: "Detail Surah"

        val tempatTurun = intent.getStringExtra(
            "TEMPAT_TURUN"
        ) ?: ""

        val artiSurah = intent.getStringExtra(
            "ARTI_SURAH"
        ) ?: ""

        // =====================================================
        // TOOLBAR
        // =====================================================

        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {

            title = namaSurah

            setDisplayHomeAsUpEnabled(true)
        }

        // Tombol panah kembali
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // =====================================================
        // TOMBOL BACK HP
        // =====================================================

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    finish()
                }
            }
        )

        // =====================================================
        // DATA SURAH
        // =====================================================

        binding.apply {

            tvSurahNameDetail.text = namaSurah

            tvSurahInfo.text =
                if (tempatTurun.isNotEmpty()) {
                    "$tempatTurun • $artiSurah"
                } else {
                    artiSurah
                }

            tvBismillah.text =
                "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ"
        }

        // =====================================================
        // RECYCLERVIEW
        // =====================================================

        setupRecyclerView()

        // =====================================================
        // LOAD AYAT
        // =====================================================

        if (idSurah != 0) {

            loadAyat(idSurah)

        } else {

            Toast.makeText(
                this,
                "ID Surah tidak valid",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }

    // =========================================================
    // SETUP RECYCLERVIEW
    // =========================================================

    private fun setupRecyclerView() {

        binding.rvAyat.apply {

            layoutManager = LinearLayoutManager(
                this@DetailSurahActivity
            )

            // Biarkan RecyclerView melakukan scrolling normal
            isNestedScrollingEnabled = true

            // Jangan paksa ukuran tetap
            setHasFixedSize(false)

            clipToPadding = false
        }
    }

    // =========================================================
    // LOAD AYAT DARI API
    // =========================================================

    private fun loadAyat(idSurah: Int) {

        lifecycleScope.launch {

            showLoading(true)

            try {

                // =================================================
                // PANGGIL CONTROLLER
                // =================================================

                val listAyat =
                    controller.getAyatBySurah(idSurah)

                // =================================================
                // CEK ACTIVITY
                // =================================================

                if (isFinishing || isDestroyed) {
                    return@launch
                }

                // =================================================
                // CEK DATA
                // =================================================

                if (!listAyat.isNullOrEmpty()) {

                    // Tampilkan data ayat
                    binding.rvAyat.adapter =
                        AyatAdapter(listAyat)

                } else {

                    Toast.makeText(
                        this@DetailSurahActivity,
                        "Ayat tidak ditemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                // =================================================
                // ERROR API
                // =================================================

                Toast.makeText(
                    this@DetailSurahActivity,
                    "Gagal mengambil data ayat: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                // =================================================
                // SELESAI LOADING
                // =================================================

                if (!isFinishing && !isDestroyed) {
                    showLoading(false)
                }
            }
        }
    }

    // =========================================================
    // LOADING
    // =========================================================

    private fun showLoading(status: Boolean) {

        binding.apply {

            if (status) {

                // Tampilkan loading
                loadingContainer.visibility =
                    View.VISIBLE

                // Sembunyikan RecyclerView sementara
                rvAyat.visibility =
                    View.INVISIBLE

            } else {

                // Sembunyikan loading
                loadingContainer.visibility =
                    View.GONE

                // Tampilkan RecyclerView
                rvAyat.visibility =
                    View.VISIBLE
            }
        }
    }
}