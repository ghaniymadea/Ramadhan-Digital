package com.pemula.ramadhandigital

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.KegiatanUserAdapter
import com.pemula.ramadhandigital.controller.*
import com.pemula.ramadhandigital.databinding.ActivityKegiatanBinding
import com.pemula.ramadhandigital.model.*
import kotlinx.coroutines.launch

class KegiatanUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKegiatanBinding
    private val kegiatanController = KegiatanUserController()
    private val ibadahController = IbadahHarianController()
    private val sunnahController = IbadahSunnahController()
    private val setoranController = SetoranHafalanController()
    private val surahController = SurahController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKegiatanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kategori = intent.getStringExtra("KATEGORI") ?: "Kegiatan Ramadhan"
        setupToolbar(kategori)

        // FAB (+) hanya muncul untuk kategori yang butuh input mandiri selain Tausiah 🍌
        if (kategori.contains("IBADAH") || kategori.contains("APRSIASI") || kategori == "SETORAN HAFALAN") {
            binding.fabAdd.visibility = View.VISIBLE
            binding.fabAdd.setOnClickListener {
                when (kategori) {
                    "CATATAN APRESIASI IBADAH HARIAN" -> showIbadahDialog()
                    "CATATAN APRSIASI IBADAH SUNNAH RAMADHAN" -> showSunnahDialog()
                    "SETORAN HAFALAN" -> showSetoranDialog()
                }
            }
        }

        loadKegiatan(kategori)
    }

    private fun setupToolbar(title: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadKegiatan(kategori: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val listKegiatanUser = when (kategori) {
                    "CATATAN KEGIATAN PESANTREN RAMADHAN" -> {
                        kegiatanController.getAllKegiatan()?.map { KegiatanUser(0, Account.Id, it.id, null, null, it) }
                    }
                    "CATATAN APRESIASI IBADAH HARIAN" -> {
                        ibadahController.getIbadahHarianHariIni()?.map {
                            KegiatanUser(it.id, it.idUser, 0, "", null, Kegiatan(0, "Ibadah Harian", "Target: ${it.targetBacaan}", it.tanggal, null, if (it.membacaAlquran) "Al-Quran: Ya" else "Al-Quran: Tidak"))
                        }
                    }
                    "CATATAN APRSIASI IBADAH SUNNAH RAMADHAN" -> {
                        sunnahController.getMyIbadahSunnahHariIni()?.map {
                            KegiatanUser(it.id, it.idUser, 0, "", null, Kegiatan(0, it.kategori?.nama ?: "Sunnah", "Mandiri", it.tanggal, null, "Dikerjakan"))
                        }
                    }
                    "SETORAN HAFALAN" -> {
                        setoranController.getSetoranByUser(Account.Id)?.map {
                            KegiatanUser(it.id, it.idUser, 0, it.note ?: "", null, Kegiatan(0, "Hafalan: ${it.surah?.surahName}", "Status: ${it.status?.nama}", it.tanggalSetoran, null, "Detail"))
                        }
                    }
                    else -> kegiatanController.getKegiatanUser(Account.Id)
                }

                binding.progressBar.visibility = View.GONE
                if (listKegiatanUser != null && listKegiatanUser.isNotEmpty()) {
                    val adapter = KegiatanUserAdapter(listKegiatanUser) { item ->
                        if (kategori.contains("IBADAH")) showIbadahDialog()
                        else showNoteDialog(item.idKegiatan, item.kegiatan?.judul ?: "Kegiatan")
                    }
                    binding.rvKegiatan.layoutManager = LinearLayoutManager(this@KegiatanUserActivity)
                    binding.rvKegiatan.adapter = adapter
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    // ... Dialog-dialog lainnya tetap sama ...
    private fun showIbadahDialog() { /* ... */ }
    private fun showSunnahDialog() { /* ... */ }
    private fun showSetoranDialog() { /* ... */ }
    private fun showNoteDialog(id: Int, judul: String) { /* ... */ }
    private fun simpanKeAPI(id: Int, note: String) { /* ... */ }
}
