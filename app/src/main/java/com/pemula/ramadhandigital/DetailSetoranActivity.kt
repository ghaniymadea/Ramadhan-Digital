package com.pemula.ramadhandigital

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.pemula.ramadhandigital.databinding.ActivityDetailSetoranBinding
import com.pemula.ramadhandigital.model.SetoranHafalan
import java.text.SimpleDateFormat
import java.util.*

class DetailSetoranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSetoranBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSetoranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        displayData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Hafalan"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun displayData() {
        val json = intent.getStringExtra("ITEM_JSON")
        val item = try {
            Gson().fromJson(json, SetoranHafalan::class.java)
        } catch (e: Exception) {
            null
        }

        if (item == null) {
            finish()
            return
        }

        binding.apply {
            val surahName = item.surah?.surahName ?: "Surah (ID: ${item.idSurah})"
            
            // LOGIKA STATUS: Prioritas ID -> Objek Nama -> Default 🛡️
            val statusNama = when (item.idStatusSetoranHafalan) {
                1 -> "Tuntas"
                2 -> "Belum Tuntas"
                else -> item.status?.nama ?: "Belum Tuntas"
            }
            
            val niceDate = formatNiceDate(item.tanggalSetoran)

            // Header Info
            tvDetailSurahName.text = surahName
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
            tvInfoJenis.text = surahName
            tvInfoBacaan.text = if (item.idBacaanSholat != null) "ID: ${item.idBacaanSholat}" else "-"
            tvInfoStatus.text = statusNama
            tvInfoStatus.setTextColor(Color.parseColor(color))
            tvInfoCatatan.text = item.note ?: "Belum ada catatan dari pembimbing."

            // Row Ringkasan
            tvTableSurah.text = surahName
            tvTableBacaan.text = item.idBacaanSholat?.toString() ?: "-"
            tvTableStatus.text = statusNama
            tvTableStatus.setTextColor(Color.parseColor(color))
        }
    }

    private fun formatNiceDate(dateStr: String?): String {
        if (dateStr == null) return "-"
        return try {
            val cleanDate = if (dateStr.contains("T")) dateStr.split("T")[0] else dateStr
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val output = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val date = input.parse(cleanDate)
            output.format(date!!)
        } catch (e: Exception) { dateStr ?: "-" }
    }
}
