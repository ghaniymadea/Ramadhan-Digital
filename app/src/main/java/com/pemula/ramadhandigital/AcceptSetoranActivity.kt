package com.pemula.ramadhandigital

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.AcceptSetoranAdapter
import com.pemula.ramadhandigital.controller.BacaanSholatController
import com.pemula.ramadhandigital.controller.SetoranHafalanController
import com.pemula.ramadhandigital.controller.SurahController
import com.pemula.ramadhandigital.databinding.ActivityAcceptSetoranBinding
import com.pemula.ramadhandigital.model.BacaanSholat
import com.pemula.ramadhandigital.model.SetoranHafalan
import com.pemula.ramadhandigital.model.Surah
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AcceptSetoranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAcceptSetoranBinding
    private val controller = SetoranHafalanController()
    private val surahController = SurahController()
    private val bacaanController = BacaanSholatController()
    private var setoranType: String = "SURAH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcceptSetoranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil tipe setoran dari intent 🚀
        setoranType = intent.getStringExtra("TYPE") ?: "SURAH"

        setupToolbar()
        loadSetoranData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Judul dinamis sesuai kategori
        supportActionBar?.title = if (setoranType == "SURAH") "Validasi Setoran Surah" else "Validasi Setoran Sholat"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadSetoranData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // 1. Ambil riwayat setoran (sesuai tipe), master surah, dan bacaan sholat secara parallel 🚀
                val dataJob = async { controller.getDaftarSetoran(setoranType) }
                val surahJob = async { surahController.getJuzAmma() }
                val bacaanJob = async { bacaanController.getBacaanSholat() }

                val listSetoranRaw = dataJob.await()
                val masterSurah = surahJob.await() ?: listOf()
                val masterBacaan = bacaanJob.await() ?: listOf()

                binding.progressBar.visibility = View.GONE

                if (!listSetoranRaw.isNullOrEmpty()) {
                    // 2. Map data: Gabungkan info Surah dan Bacaan Sholat agar tampil namanya 🔍
                    val finalData = listSetoranRaw.map { item ->
                        
                        // Handle mapping Surah
                        val updatedSurah = if (item.idSurah != null && item.idSurah != 0) {
                            val surahNameMatch = masterSurah.find { it.id == item.idSurah }?.surahName
                            if (item.surah == null) {
                                Surah(id = item.idSurah!!, surahName = surahNameMatch, artiSurat = null, tempatTurun = null, nomor = 0)
                            } else if (item.surah.surahName == null) {
                                item.surah.copy(surahName = surahNameMatch)
                            } else {
                                item.surah
                            }
                        } else {
                            item.surah
                        }

                        // Handle mapping Bacaan Sholat
                        val updatedBacaan = if (item.idBacaanSholat != null && item.idBacaanSholat != 0) {
                            val bacaanNameMatch = masterBacaan.find { it.id == item.idBacaanSholat }?.nama
                            if (item.bacaanSholat == null) {
                                BacaanSholat(id = item.idBacaanSholat, nama = bacaanNameMatch, arabic = null, translate = null, urutan = null)
                            } else if (item.bacaanSholat?.nama == null) {
                                item.bacaanSholat?.copy(nama = bacaanNameMatch)
                            } else {
                                item.bacaanSholat
                            }
                        } else {
                            item.bacaanSholat
                        }

                        item.copy(surah = updatedSurah, bacaanSholat = updatedBacaan)
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
            // Menambahkan parameter type (SURAH / BACAAN_SHOLAT) agar sinkron dengan endpoint 🛠️
            val sukses = controller.updateStatusSetoran(setoran.id, setoranUpdate, setoranType)
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
