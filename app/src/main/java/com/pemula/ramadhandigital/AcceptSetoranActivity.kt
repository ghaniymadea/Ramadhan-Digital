package com.pemula.ramadhandigital

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.AcceptSetoranAdapter
import com.pemula.ramadhandigital.controller.SetoranHafalanController
import com.pemula.ramadhandigital.controller.SurahController
import com.pemula.ramadhandigital.databinding.ActivityAcceptSetoranBinding
import com.pemula.ramadhandigital.model.SetoranHafalan
import com.pemula.ramadhandigital.model.Surah
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AcceptSetoranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAcceptSetoranBinding
    private val controller = SetoranHafalanController()
    private val surahController = SurahController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcceptSetoranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadSetoranData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Judul otomatis dari XML: "Validasi Setoran Hafalan" 🚀
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadSetoranData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // 1. Ambil riwayat setoran dan master surah secara parallel agar cepat 🚀
                val dataJob = async { controller.getDaftarSetoran() }
                val surahJob = async { surahController.getJuzAmma() }

                val listSetoranRaw = dataJob.await()
                val masterSurah = surahJob.await() ?: listOf()

                binding.progressBar.visibility = View.GONE

                if (!listSetoranRaw.isNullOrEmpty()) {
                    // 2. Map data: Pastikan Nama Surah muncul (tidak null) 🔍
                    val finalData = listSetoranRaw.map { item ->
                        val match = masterSurah.find { it.id == item.idSurah }
                        val surahNameMatch = match?.surahName
                        
                        val updatedSurah = if (item.surah == null) {
                            Surah(id = item.idSurah, surahName = surahNameMatch, artiSurat = null, tempatTurun = null, nomor = 0)
                        } else if (item.surah.surahName == null) {
                            item.surah.copy(surahName = surahNameMatch)
                        } else {
                            item.surah
                        }
                        item.copy(surah = updatedSurah)
                    }

                    val adapter = AcceptSetoranAdapter(finalData) { setoran ->
                        // Verifikasi hafalan: Set ID Status ke 1 (Tuntas) ✅
                        updateStatus(setoran, 1)
                    }
                    binding.rvSetoran.layoutManager = LinearLayoutManager(this@AcceptSetoranActivity)
                    binding.rvSetoran.adapter = adapter
                } else {
                    Toast.makeText(this@AcceptSetoranActivity, "Belum ada setoran yang masuk", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Log.e("AcceptSetoran", "Error: ${e.message}")
            }
        }
    }

    private fun updateStatus(setoran: SetoranHafalan, idStatusBaru: Int) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val setoranUpdate = setoran.copy(idStatusSetoranHafalan = idStatusBaru)
            val sukses = controller.updateStatusSetoran(setoran.id, setoranUpdate)
            binding.progressBar.visibility = View.GONE

            if (sukses) {
                Toast.makeText(this@AcceptSetoranActivity, "Hafalan Berhasil Diverifikasi! ✅", Toast.LENGTH_SHORT).show()
                loadSetoranData() // Refresh daftar
            } else {
                Toast.makeText(this@AcceptSetoranActivity, "Gagal memverifikasi hafalan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
