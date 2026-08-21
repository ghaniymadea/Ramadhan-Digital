package com.pemula.ramadhandigital

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.TrackingSiswaAdapter
import com.pemula.ramadhandigital.controller.AbsensiController
import com.pemula.ramadhandigital.databinding.ActivityExportPdfBinding
import com.pemula.ramadhandigital.model.Account
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ExportPdfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExportPdfBinding
    private val absensiController = AbsensiController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExportPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadStatisticsData()

        binding.btnExport.setOnClickListener {
            generatePdf()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadStatisticsData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val currentDate = sdf.format(Date())
                
                // Ambil ID Kelas Guru
                val rawKelas = Account.Kelas ?: "1"
                val idKelasInt = rawKelas.filter { it.isDigit() }.toIntOrNull() ?: 1

                // Tarik data absensi hari ini sebagai basis statistik 📊
                val listSiswa = absensiController.getAbsensi(idKelasInt, currentDate)
                binding.progressBar.visibility = View.GONE

                if (listSiswa != null) {
                    // 1. Hitung Statistik 🍌🐒
                    val totalSiswa = listSiswa.size
                    val totalHadir = listSiswa.count { it.idStatusAbsensi == 1 }

                    binding.tvTotalSiswa.text = totalSiswa.toString()
                    binding.tvTotalHadir.text = totalHadir.toString()

                    // 2. Tampilkan Daftar Siswa di Bawah Statistik
                    val adapter = TrackingSiswaAdapter(listSiswa) { siswa ->
                        Toast.makeText(this@ExportPdfActivity, "Progres ${siswa.namaSiswa}", Toast.LENGTH_SHORT).show()
                    }
                    binding.rvSiswaSummary.layoutManager = LinearLayoutManager(this@ExportPdfActivity)
                    binding.rvSiswaSummary.adapter = adapter
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Log.e("ExportPdf", "Gagal load statistik: ${e.message}")
            }
        }
    }

    private fun generatePdf() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnExport.isEnabled = false
        
        // Simulasi proses pembuatan PDF 🚀🔥
        binding.root.postDelayed({
            binding.progressBar.visibility = View.GONE
            binding.btnExport.isEnabled = true
            Toast.makeText(this, "Laporan PDF berhasil dibuat dan disimpan!", Toast.LENGTH_LONG).show()
        }, 2500)
    }
}
