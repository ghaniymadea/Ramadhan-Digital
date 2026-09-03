package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.SetoranHafalanAdapter
import com.pemula.ramadhandigital.controller.BacaanSholatController
import com.pemula.ramadhandigital.controller.SetoranHafalanController
import com.pemula.ramadhandigital.controller.SurahController
import com.pemula.ramadhandigital.databinding.ActivitySetoranHafalanBinding
import com.pemula.ramadhandigital.model.*
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SetoranHafalanActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetoranHafalanBinding
    private val controller = SetoranHafalanController()
    private val surahController = SurahController()
    private val bacaanController = BacaanSholatController()
    private var setoranType: String = "SURAH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetoranHafalanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pastikan Session tersinkron ke Account jika state memory hilang 🛡️
        SessionManager(this).syncToAccount()

        setoranType = intent.getStringExtra("TYPE") ?: "SURAH"

        setupToolbar()
        setupSwipeRefresh()
        loadData(showProgress = true)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (setoranType == "SURAH") "Setoran Juz Amma" else "Setoran Bacaan Sholat"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeColors("#2E7D32".toColorInt())
        binding.swipeRefresh.setOnRefreshListener {
            loadData(showProgress = false)
        }
    }

    private fun loadData(showProgress: Boolean) {
        if (showProgress) binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                // 1. Ambil ID User dari Token (antisipasi ID 0 di memory) 🚀
                val idUser = Account.getUserIdFromToken()
                
                if (idUser == 0) {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(this@SetoranHafalanActivity, "Sesi berakhir, silakan login ulang", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Ambil data riwayat (menggunakan endpoint baru sesuai type), master surah, dan bacaan secara parallel agar cepat 🚀
                val dataJob = async { controller.getSetoranSiswa(idUser, setoranType) }
                val surahJob = async { surahController.getJuzAmma() }
                val bacaanJob = async { bacaanController.getBacaanSholat() }

                val rawHistory = dataJob.await() ?: listOf()
                val masterSurah = surahJob.await() ?: listOf()
                val masterBacaan = bacaanJob.await() ?: listOf()

                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                
                val finalData = mutableListOf<SetoranHafalan>()

                if (setoranType == "SURAH") {
                    // Tampilkan SEMUA surah dari master data 📖
                    masterSurah.forEach { surah ->
                        // Cari apakah sudah ada riwayat setoran untuk surah ini
                        val match = rawHistory.filter { it.idSurah == surah.id }
                            .sortedBy { it.idStatusSetoranHafalan } // Prioritaskan status 1 (Tuntas)
                            .firstOrNull()

                        if (match != null) {
                            finalData.add(match.copy(surah = surah))
                        } else {
                            // Jika belum pernah setor, status default: Belum Tuntas (2)
                            finalData.add(SetoranHafalan(
                                id = 0,
                                idUser = idUser,
                                idSurah = surah.id,
                                idBacaanSholat = null,
                                idStatusSetoranHafalan = 2,
                                note = null,
                                tanggalSetoran = null,
                                surah = surah
                            ))
                        }
                    }
                } else {
                    // Tampilkan SEMUA bacaan sholat dari master data 🕌
                    masterBacaan.forEach { bacaan ->
                        val match = rawHistory.filter { it.idBacaanSholat == bacaan.id }
                            .sortedBy { it.idStatusSetoranHafalan }
                            .firstOrNull()

                        if (match != null) {
                            finalData.add(match.copy(bacaanSholat = bacaan))
                        } else {
                            // Default status: Belum Tuntas (2)
                            finalData.add(SetoranHafalan(
                                id = 0,
                                idUser = idUser,
                                idSurah = null,
                                idBacaanSholat = bacaan.id,
                                idStatusSetoranHafalan = 2,
                                note = null,
                                tanggalSetoran = null,
                                bacaanSholat = bacaan
                            ))
                        }
                    }
                }

                if (finalData.isNotEmpty()) {
                    val adapter = SetoranHafalanAdapter(finalData) { item ->
                        if (item.id != 0) {
                            val intent = Intent(this@SetoranHafalanActivity, DetailSetoranActivity::class.java).apply {
                                putExtra("ITEM_JSON", Gson().toJson(item))
                            }
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@SetoranHafalanActivity, "Hafalan ini belum disetorkan ke Pembimbing ✨", Toast.LENGTH_SHORT).show()
                        }
                    }
                    binding.rvSetoran.layoutManager = LinearLayoutManager(this@SetoranHafalanActivity)
                    binding.rvSetoran.adapter = adapter
                    binding.rvSetoran.visibility = View.VISIBLE
                } else {
                    binding.rvSetoran.visibility = View.GONE
                    Toast.makeText(this@SetoranHafalanActivity, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(this@SetoranHafalanActivity, "Gagal sinkron data: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
