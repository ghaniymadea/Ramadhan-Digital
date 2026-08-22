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
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val currentDate = sdf.format(Date())
                
                // AMBIL ID KELAS LANGSUNG DARI ACCOUNT 🍌🚀
                // Sekarang jauh lebih aman karena data diambil dari Login Response (Integer)
                val idKelasInt = Account.IdKelas

                val listSiswa = absensiController.getAbsensi(idKelasInt, currentDate)
                binding.progressBar.visibility = View.GONE

                if (listSiswa != null) {
                    val adapter = TrackingSiswaAdapter(listSiswa) { siswa ->
                        // Detail progress per siswa 🍌🔥
                        Toast.makeText(this@TrackingSiswaActivity, "Detail progress ${siswa.namaSiswa}", Toast.LENGTH_SHORT).show()
                    }
                    binding.rvTracking.layoutManager = LinearLayoutManager(this@TrackingSiswaActivity)
                    binding.rvTracking.adapter = adapter
                } else {
                    Toast.makeText(this@TrackingSiswaActivity, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Log.e("TrackingSiswa", "Error Load: ${e.message}")
                Toast.makeText(this@TrackingSiswaActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
