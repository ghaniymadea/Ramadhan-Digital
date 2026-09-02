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
import com.pemula.ramadhandigital.controller.SetoranHafalanController
import com.pemula.ramadhandigital.controller.SurahController
import com.pemula.ramadhandigital.databinding.ActivitySetoranHafalanBinding
import com.pemula.ramadhandigital.model.SetoranHafalan
import com.pemula.ramadhandigital.model.Surah
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SetoranHafalanActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetoranHafalanBinding
    private val controller = SetoranHafalanController()
    private val surahController = SurahController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetoranHafalanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSwipeRefresh()
        loadData(showProgress = true)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
                // 1. Ambil data setoran dan daftar surah secara parallel 🚀
                val dataDeferred = async { controller.getDaftarSetoran() }
                val surahDeferred = async { surahController.getJuzAmma() }

                val rawData = dataDeferred.await()
                val masterSurah = surahDeferred.await() ?: listOf()

                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                
                if (!rawData.isNullOrEmpty()) {
                    // 2. Map data: Pastikan Nama Surah terisi meskipun backend mengirim null 🔍
                    val finalData = rawData.map { item ->
                        val match = masterSurah.find { it.id == item.idSurah }
                        val surahNameMatch = match?.surahName
                        
                        // Jika objek surah null atau surahName-nya null, kita buat objek baru dengan nama yang benar 🛡️
                        val updatedSurah = if (item.surah == null) {
                            Surah(id = item.idSurah, surahName = surahNameMatch, artiSurat = null, tempatTurun = null, nomor = 0)
                        } else if (item.surah.surahName == null) {
                            item.surah.copy(surahName = surahNameMatch)
                        } else {
                            item.surah
                        }
                        
                        item.copy(surah = updatedSurah)
                    }

                    val adapter = SetoranHafalanAdapter(finalData) { item ->
                        val intent = Intent(this@SetoranHafalanActivity, DetailSetoranActivity::class.java).apply {
                            putExtra("ITEM_JSON", Gson().toJson(item))
                        }
                        startActivity(intent)
                    }
                    binding.rvSetoran.layoutManager = LinearLayoutManager(this@SetoranHafalanActivity)
                    binding.rvSetoran.adapter = adapter
                } else {
                    Toast.makeText(this@SetoranHafalanActivity, "Belum ada riwayat setoran", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(this@SetoranHafalanActivity, "Gagal sinkron data: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
