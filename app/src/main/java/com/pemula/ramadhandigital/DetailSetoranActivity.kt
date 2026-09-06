package com.pemula.ramadhandigital

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.pemula.ramadhandigital.controller.SetoranHafalanController
import com.pemula.ramadhandigital.databinding.ActivityDetailSetoranBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.SetoranHafalan
import com.pemula.ramadhandigital.utils.DateHelper
import kotlinx.coroutines.launch
import java.util.*

class DetailSetoranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSetoranBinding
    private val controller = SetoranHafalanController()
    private var currentItem: SetoranHafalan? = null
    private var setoranType: String = "SURAH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSetoranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        displayData()
        setupAksiGuru()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Hafalan"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun displayData() {
        val json = intent.getStringExtra("ITEM_JSON")
        setoranType = if (json?.contains("idSurah") == true && !json.contains("\"idSurah\":null")) "SURAH" else "BACAAN_SHOLAT"
        
        val item = try {
            Gson().fromJson(json, SetoranHafalan::class.java)
        } catch (e: Exception) {
            null
        }

        if (item == null) {
            finish()
            return
        }

        currentItem = item

        binding.apply {
            val title = when {
                item.surah != null -> item.surah.surahName ?: "Surah (ID: ${item.idSurah})"
                item.bacaanSholat != null -> item.bacaanSholat.nama ?: "Bacaan (ID: ${item.idBacaanSholat})"
                item.idSurah != null && item.idSurah != 0 -> "Surah (ID: ${item.idSurah})"
                item.idBacaanSholat != null && item.idBacaanSholat != 0 -> "Bacaan (ID: ${item.idBacaanSholat})"
                else -> "Setoran Hafalan"
            }
            
            val statusNama = when (item.idStatusSetoranHafalan) {
                1 -> "Tuntas"
                2 -> "Belum Tuntas"
                else -> item.status?.nama ?: "Belum Tuntas"
            }
            
            val niceDate = DateHelper.toDisplayDate(item.tanggalSetoran)

            // Header Info
            tvDetailSurahName.text = title
            tvDetailSubInfo.text = if (item.idBacaanSholat != null) "Materi: Bacaan Sholat" else "Materi: Juz Amma"
            tvBadgeStatus.text = statusNama.uppercase()
            tvDetailDateTop.text = niceDate
            
            // Warna Badge Dinamis 🎨
            val isTuntas = statusNama.lowercase().trim() == "tuntas"
            val color = if (isTuntas) "#059669" else "#DC2626"
            
            tvBadgeStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
            tvBadgeStatus.setTextColor(Color.WHITE)

            // Tabel Informasi
            tvInfoTanggal.text = niceDate
            tvInfoJenis.text = title
            tvInfoStatus.text = statusNama
            tvInfoStatus.setTextColor(Color.parseColor(color))
            tvInfoCatatan.text = item.note ?: "Belum ada catatan dari pembimbing."
        }
    }

    private fun setupAksiGuru() {
        if (Account.isGuru()) {
            binding.layoutAksiGuru.visibility = View.VISIBLE
            
            binding.btnEdit.setOnClickListener {
                val intent = Intent(this, AddSetoranGuruActivity::class.java).apply {
                    putExtra("TYPE", setoranType)
                    putExtra("EDIT_MODE", true)
                    putExtra("ITEM_JSON", Gson().toJson(currentItem))
                }
                startActivity(intent)
                finish() // Tutup detail setelah buka form edit 🚀
            }

            binding.btnHapus.setOnClickListener {
                showDeleteConfirmation()
            }
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Setoran")
            .setMessage("Apakah Anda yakin ingin menghapus data setoran ini? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { _, _ ->
                deleteSetoran()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteSetoran() {
        val id = currentItem?.id ?: 0
        if (id == 0) return

        lifecycleScope.launch {
            val success = controller.deleteSetoran(id, setoranType)
            if (success) {
                Toast.makeText(this@DetailSetoranActivity, "Data berhasil dihapus ✅", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@DetailSetoranActivity, "Gagal menghapus data dari server.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
