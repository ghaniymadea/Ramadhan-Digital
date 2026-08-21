package com.pemula.ramadhandigital

import android.content.Intent
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
                val data = controller.getSetoranByUser(Account.Id)
                binding.progressBar.visibility = View.GONE
                
                if (!data.isNullOrEmpty()) {
                    // Konversi ke model KegiatanUser agar bisa pakai adapter yang sudah ada 🚀
                    val list = data.map {
                        KegiatanUser(
                            id = it.id,
                            idUser = it.idUser,
                            idKegiatan = it.idSurah, // Simpan ID Surah di sini
                            note = it.note ?: "",
                            user = null,
                            kegiatan = Kegiatan(
                                id = it.idSurah,
                                judul = it.surah?.surahName ?: "Surah Tidak Diketahui",
                                pemateri = it.status?.nama ?: "Proses",
                                tanggal = it.tanggalSetoran,
                                kegiatanUsers = null,
                                jam = "ID: ${it.idBacaanSholat ?: "-"}"
                            )
                        )
                    }

                    val adapter = KegiatanUserAdapter(list) { item ->
                        // PINDAH KE DETAIL SETORAN DENGAN STYLE BARU 🚀🔥
                        val intent = Intent(this@SetoranHafalanActivity, DetailSetoranActivity::class.java).apply {
                            putExtra("SURAH_NAME", item.kegiatan?.judul)
                            putExtra("BACAAN_ID", item.kegiatan?.jam?.replace("ID: ", "")?.toIntOrNull() ?: 0)
                            putExtra("STATUS", item.kegiatan?.pemateri)
                            putExtra("DATE", item.kegiatan?.tanggal)
                            putExtra("NOTE", item.note)
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
                Toast.makeText(this@SetoranHafalanActivity, "Gagal memuat riwayat", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
