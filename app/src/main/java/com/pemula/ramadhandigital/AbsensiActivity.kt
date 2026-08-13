package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.AbsensiAdapter
import com.pemula.ramadhandigital.controller.AbsensiController
import com.pemula.ramadhandigital.databinding.ActivityAbsensiBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.PostAbsensiItem
import com.pemula.ramadhandigital.model.PostAbsensiRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AbsensiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsensiBinding
    private val controller = AbsensiController()
    private var listSiswa: List<com.pemula.ramadhandigital.model.AbsensiItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsensiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        // Contoh tanggal hari ini 🍌
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentData = sdf.format(Date())
        binding.tvTanggal.text = "Tanggal: $currentData"

        val idKelas = Account.Kelas ?: "1"
        loadAbsensi(idKelas, currentData)

        binding.btnSimpan.setOnClickListener {
            simpanAbsensi(currentData)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadAbsensi(idKelas: String, tanggal: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            listSiswa = controller.getAbsensi(idKelas, tanggal)
            binding.progressBar.visibility = View.GONE

            if (listSiswa != null) {
                val adapter = AbsensiAdapter(listSiswa!!)
                binding.rvAbsensi.layoutManager = LinearLayoutManager(this@AbsensiActivity)
                binding.rvAbsensi.adapter = adapter
            } else {
                Toast.makeText(this@AbsensiActivity, "Gagal mengambil data siswa", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun simpanAbsensi(tanggal: String) {
        if (listSiswa == null) return

        binding.progressBar.visibility = View.VISIBLE
        
        // Monyet bungkus datanya buat dikirim ke API 🐒
        val items = listSiswa!!.map { 
            PostAbsensiItem(it.idUser, it.idStatusAbsensi) 
        }
        
        val request = PostAbsensiRequest(
            tanggal = "${tanggal}T00:00:00Z",
            siswaList = items
        )

        lifecycleScope.launch {
            val sukses = controller.simpanAbsensi(request)
            binding.progressBar.visibility = View.GONE
            
            if (sukses) {
                Toast.makeText(this@AbsensiActivity, "Absensi berhasil disimpan!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@AbsensiActivity, "Gagal menyimpan absensi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}