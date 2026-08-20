package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.KegiatanUserAdapter
import com.pemula.ramadhandigital.controller.SetoranHafalanController
import com.pemula.ramadhandigital.databinding.ActivitySetoranHafalanBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.Kegiatan
import com.pemula.ramadhandigital.model.KegiatanUser
import kotlinx.coroutines.launch

class SetoranHafalanActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetoranHafalanBinding
    private val controller = SetoranHafalanController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetoranHafalanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Riwayat Setoran Hafalan"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // Ambil data setoran dari server 🍌🐒
                val data = controller.getSetoranByUser(Account.Id)
                binding.progressBar.visibility = View.GONE
                
                // Konversi ke model KegiatanUser agar bisa pakai adapter yang sudah ada 🚀
                val list = data?.map {
                    KegiatanUser(
                        id = it.id,
                        idUser = it.idUser,
                        idKegiatan = 0,
                        note = it.note ?: "",
                        user = null,
                        kegiatan = Kegiatan(
                            id = 0,
                            judul = "Hafalan: ${it.surah?.surahName ?: "Surah"}",
                            pemateri = "Status: ${it.status?.nama ?: "Proses"}",
                            tanggal = it.tanggalSetoran,
                            kegiatanUsers = null,
                            jam = "Detail"
                        )
                    )
                }

                if (!list.isNullOrEmpty()) {
                    val adapter = KegiatanUserAdapter(list) { item ->
                        Toast.makeText(this@SetoranHafalanActivity, "Note: ${item.note}", Toast.LENGTH_LONG).show()
                    }
                    binding.rvSetoran.layoutManager = LinearLayoutManager(this@SetoranHafalanActivity)
                    binding.rvSetoran.adapter = adapter
                } else {
                    Toast.makeText(this@SetoranHafalanActivity, "Belum ada riwayat setoran", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}
