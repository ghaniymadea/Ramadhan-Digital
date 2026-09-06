package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.KegiatanUserAdapter
import com.pemula.ramadhandigital.controller.KegiatanUserController
import com.pemula.ramadhandigital.databinding.ActivityDetailKegiatanSiswaBinding
import com.pemula.ramadhandigital.model.KegiatanUser
import kotlinx.coroutines.launch

class DetailKegiatanSiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKegiatanSiswaBinding
    private val kegiatanController = KegiatanUserController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailKegiatanSiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idUser = intent.getIntExtra("ID_USER", 0)
        val namaSiswa = intent.getStringExtra("NAMA_SISWA") ?: "Siswa"

        setupToolbar(namaSiswa)
        loadDetailKegiatan(idUser, namaSiswa)
    }

    private fun setupToolbar(nama: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Catatan: $nama"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadDetailKegiatan(idUser: Int, nama: String) {
        binding.tvNamaSiswaHeader.text = nama
        // Set inisial huruf depan 👤
        binding.tvDetailInitial.text = nama.take(1).uppercase()
        
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val list = kegiatanController.getKegiatanUser(idUser)
                binding.progressBar.visibility = View.GONE

                if (!list.isNullOrEmpty()) {
                    binding.tvEmptyMsg.visibility = View.GONE
                    
                    // Gunakan adapter dengan fungsi klik untuk melihat detail catatan 🐒🔥
                    val adapter = KegiatanUserAdapter(list, isGuruMode = true) { item ->
                        val intent = Intent(this@DetailKegiatanSiswaActivity, AddKegiatanActivity::class.java)
                        intent.putExtra("ID_KEGIATAN", item.idKegiatan)
                        intent.putExtra("JUDUL", item.kegiatan?.judul)
                        intent.putExtra("USTADZ", item.kegiatan?.pemateri)
                        intent.putExtra("NOTE", item.note)
                        intent.putExtra("IS_SUBMITTED", true) // Paksa Read-Only untuk Guru 🔐
                        startActivity(intent)
                    }
                    
                    binding.rvDetailKegiatan.layoutManager = LinearLayoutManager(this@DetailKegiatanSiswaActivity)
                    binding.rvDetailKegiatan.adapter = adapter
                } else {
                    binding.tvEmptyMsg.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@DetailKegiatanSiswaActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
