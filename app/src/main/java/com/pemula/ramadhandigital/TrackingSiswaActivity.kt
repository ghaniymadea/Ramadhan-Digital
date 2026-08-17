package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.TrackingSiswaAdapter
import com.pemula.ramadhandigital.controller.AbsensiController
import com.pemula.ramadhandigital.databinding.ActivityTrackingSiswaBinding
import com.pemula.ramadhandigital.model.Account
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TrackingSiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackingSiswaBinding
    private val absensiController = AbsensiController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingSiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadSiswaData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadSiswaData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // Ambil tanggal hari ini untuk parameter API 🍌
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val currentDate = sdf.format(Date())
                val idKelas = Account.Kelas ?: "1"

                // Monyet ganti getSiswa() jadi getAbsensi() karena itu yang ada di Controller! 🐒🔥
                val listSiswa = absensiController.getAbsensi(idKelas, currentDate)
                binding.progressBar.visibility = View.GONE

                if (listSiswa != null) {
                    val adapter = TrackingSiswaAdapter(listSiswa) { siswa ->
                        // Nanti arahkan ke detail progress per siswa 🍌🔥
                        Toast.makeText(this@TrackingSiswaActivity, "Detail progress ${siswa.namaSiswa}", Toast.LENGTH_SHORT).show()
                    }
                    binding.rvTracking.layoutManager = LinearLayoutManager(this@TrackingSiswaActivity)
                    binding.rvTracking.adapter = adapter
                } else {
                    Toast.makeText(this@TrackingSiswaActivity, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@TrackingSiswaActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
