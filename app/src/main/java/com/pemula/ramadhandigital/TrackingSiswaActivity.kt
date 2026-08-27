package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.TrackingSiswaAdapter
import com.pemula.ramadhandigital.controller.IbadahHarianController
import com.pemula.ramadhandigital.databinding.ActivityTrackingSiswaBinding
import com.pemula.ramadhandigital.model.Account
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TrackingSiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackingSiswaBinding
    private val ibadahController = IbadahHarianController()

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
        // Hapus setDisplayShowTitleEnabled(false) agar judul muncul 🚀
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadSiswaData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val currentDate = sdf.format(Date())
                val idKelasInt = Account.IdKelas

                val listIbadah = ibadahController.getMonitoringKelas(idKelasInt, currentDate)
                binding.progressBar.visibility = View.GONE

                if (listIbadah != null) {
                    val adapter = TrackingSiswaAdapter(listIbadah) { item ->
                        val intent = Intent(this@TrackingSiswaActivity, DetailKegiatanSiswaActivity::class.java)
                        intent.putExtra("ID_USER", item.idUser)
                        intent.putExtra("NAMA_SISWA", item.namaUser)
                        startActivity(intent)
                    }
                    binding.rvTracking.layoutManager = LinearLayoutManager(this@TrackingSiswaActivity)
                    binding.rvTracking.adapter = adapter
                } else {
                    Toast.makeText(this@TrackingSiswaActivity, "Data tidak ditemukan (404/Empty)", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Log.e("TrackingSiswa", "Error: ${e.message}")
            }
        }
    }
}
