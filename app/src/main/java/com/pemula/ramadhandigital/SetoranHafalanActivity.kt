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
import com.pemula.ramadhandigital.controller.SurahController
import com.pemula.ramadhandigital.databinding.ActivitySetoranHafalanBinding
import com.pemula.ramadhandigital.model.Kegiatan
import com.pemula.ramadhandigital.model.KegiatanUser
import com.pemula.ramadhandigital.model.Surah
import kotlinx.coroutines.launch

class SetoranHafalanActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetoranHafalanBinding
    private val controller = SetoranHafalanController()
    private val surahController = SurahController()
    private var listSurah = listOf<Surah>()

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
                // 1. Ambil daftar surah dulu agar kita punya referensi nama surah 📖
                listSurah = surahController.getJuzAmma() ?: listOf()

                // 2. Ambil data setoran siswa
                val data = controller.getDaftarSetoran()
                binding.progressBar.visibility = View.GONE
                
                if (!data.isNullOrEmpty()) {
                    val list = data.map { setoran ->
                        // Cari nama surah berdasarkan idSurah 🔍
                        val surahMatch = listSurah.find { it.id == setoran.idSurah }
                        val namaSurah = setoran.surah?.surahName ?: surahMatch?.surahName ?: "Surah (ID: ${setoran.idSurah})"

                        KegiatanUser(
                            id = setoran.id,
                            idUser = setoran.idUser,
                            idKegiatan = setoran.idSurah,
                            note = setoran.note ?: "",
                            user = null,
                            kegiatan = Kegiatan(
                                id = setoran.idSurah,
                                judul = namaSurah,
                                pemateri = setoran.status?.nama ?: "Proses",
                                tanggal = setoran.tanggalSetoran,
                                kegiatanUsers = null,
                                jam = "ID: ${setoran.idBacaanSholat ?: "-"}"
                            )
                        )
                    }

                    val adapter = KegiatanUserAdapter(list) { item ->
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
                Toast.makeText(this@SetoranHafalanActivity, "Gagal memuat riwayat: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
