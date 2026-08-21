package com.pemula.ramadhandigital

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pemula.ramadhandigital.databinding.ActivityDetailSetoranBinding

class DetailSetoranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSetoranBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSetoranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        displayData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Submission Detail"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun displayData() {
        // Tangkap data dari intent 📦
        val surahName = intent.getStringExtra("SURAH_NAME") ?: "Surah"
        val bacaanId = intent.getIntExtra("BACAAN_ID", 0)
        val status = intent.getStringExtra("STATUS") ?: "Proses"
        val date = intent.getStringExtra("DATE") ?: "-"
        val note = intent.getStringExtra("NOTE") ?: "Belum ada catatan."

        // Set Data ke UI 🍌🐒
        binding.apply {
            // Header
            tvDetailSurahName.text = surahName
            tvDetailSubInfo.text = "Bacaan Sholat (ID: $bacaanId)"
            tvBadgeStatus.text = status.uppercase()
            tvDetailDateTop.text = date

            // Informasi Setoran
            tvInfoTanggal.text = date
            tvInfoJenis.text = surahName
            tvInfoBacaan.text = "ID: $bacaanId"
            tvInfoStatus.text = status
            tvInfoCatatan.text = "\"$note\""

            // Ringkasan Table
            tvTableSurah.text = surahName.replace("Surah ", "")
            tvTableBacaan.text = bacaanId.toString()
            tvTableStatus.text = status
        }
    }
}