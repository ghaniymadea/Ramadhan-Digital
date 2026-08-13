package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.Toast
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
        // Paksa aplikasi selalu mode terang biar gak gelap gulita 🍌
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        
        binding = ActivityDetailSurahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Ambil semua data kiriman dari JuzAmmaActivity 🍌🐒
        // Pake ID asli database Bos!
        val idSurah = intent.getIntExtra("ID_SURAH", 0)
        val namaSurah = intent.getStringExtra("NAMA_SURAH") ?: "Detail Surah"
        val tempatTurun = intent.getStringExtra("TEMPAT_TURUN") ?: ""
        val artiSurah = intent.getStringExtra("ARTI_SURAH") ?: ""

        // 2. Pasang nama surah di Judul Atas (Toolbar)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = namaSurah
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { finish() }
        
        // 3. Pasang data ke UI 🍌✨
        binding.apply {
            tvSurahNameDetail.text = namaSurah
            tvSurahInfo.text = if (tempatTurun.isNotEmpty()) "$tempatTurun • $artiSurah" else artiSurah
            tvBismillah.text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ"
        }

        setupRecyclerView()

        if (idSurah != 0) {
            loadAyat(idSurah)
        } else {
            Toast.makeText(this, "ID Surah tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.rvAyat.apply {
            layoutManager = LinearLayoutManager(this@DetailSurahActivity)
            setHasFixedSize(true)
        }
    }

    private fun loadAyat(idSurah: Int) {
        lifecycleScope.launch {
            showLoading(true)
            try {
                // Monyet panggil sopir buat ambil data ayat dari internet 🍌
                val listAyat = controller.getAyatBySurah(idSurah)
                
                if (isFinishing || isDestroyed) return@launch

                if (!listAyat.isNullOrEmpty()) {
                    // Berhasil dapet pisang! Langsung pajang di layar 🍌
                    binding.rvAyat.adapter = AyatAdapter(listAyat)
                } else {
                    Toast.makeText(this@DetailSurahActivity, "Ayat tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailSurahActivity, "Gagal mengambil data ayat: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                // MONYET PAKE FINALLY: showLoading(false) cukup dipanggil sekali di sini! 🍌🔥
                showLoading(false)
            }
        }
    }

    private fun showLoading(status: Boolean) {
        binding.apply {
            loadingContainer.visibility = if (status) View.VISIBLE else View.GONE
            rvAyat.visibility = if (status) View.INVISIBLE else View.VISIBLE
            appBarLayout.visibility = if (status) View.INVISIBLE else View.VISIBLE
        }
    }
}
