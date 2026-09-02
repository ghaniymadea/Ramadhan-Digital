package com.pemula.ramadhandigital

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pemula.ramadhandigital.controller.IbadahSunnahController
import com.pemula.ramadhandigital.databinding.ActivityIbadahSunnahBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class IbadahSunnahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIbadahSunnahBinding
    private val controller = IbadahSunnahController()
    
    // Map untuk menyimpan status checklist (ID Kategori sesuai database 🍌)
    private val sunnahStatus = mutableMapOf<Int, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIbadahSunnahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        loadData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { finish() }
        
        // PERBAIKAN: Gunakan Locale yang benar untuk menghindari warning deprecated 🛠️
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID"))
        binding.tvDate.text = sdf.format(Date())
    }

    private fun setupClickListeners() {
        // ID disesuaikan dengan database: Tarawih(1), Witir(2), Dhuha(3), Tahajud(4), Sedekah(5) 🚀
        binding.itemTarawih.setOnClickListener { toggleSunnah(1) }
        binding.itemWitir.setOnClickListener { toggleSunnah(2) }
        binding.itemDhuha.setOnClickListener { toggleSunnah(3) }
        binding.itemTahajud.setOnClickListener { toggleSunnah(4) }
        binding.itemSedekah.setOnClickListener { toggleSunnah(5) }
        
        binding.btnSimpan.setOnClickListener {
            simpanProgress()
        }
    }

    private fun toggleSunnah(idKategori: Int) {
        val current = sunnahStatus[idKategori] ?: false
        sunnahStatus[idKategori] = !current
        updateUI()
    }

    private fun loadData() {
        binding.loadingBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val dataList = controller.getMyIbadahSunnahHariIni()
                binding.loadingBar.visibility = View.GONE
                
                sunnahStatus.clear()
                
                // Sinkronisasi data dari server 🐒
                dataList?.forEach { ibadah ->
                    // 1. Cek Flat Structure (Sesuai Monitoring Siswa) 🚀
                    if (ibadah.idKategoriSunnah != 0 && ibadah.sudahDilakukan) {
                        sunnahStatus[ibadah.idKategoriSunnah] = true
                    }
                    
                    // 2. Cek Nested Details (Jika ada)
                    ibadah.detailIbadahSunnahs?.forEach { detail ->
                        // PERBAIKAN: Pastikan menggunakan field yang benar sesuai model terbaru 🛡️
                        if (detail.idKategoriSunnah != 0 && detail.sudahDilakukan) {
                            sunnahStatus[detail.idKategoriSunnah] = true
                        }
                    }
                }
                
                updateUI()
            } catch (e: Exception) {
                binding.loadingBar.visibility = View.GONE
                Log.e("IbadahSunnah", "Error loadData: ${e.message}")
                Toast.makeText(this@IbadahSunnahActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        // Update masing-masing item UI sesuai ID database 🍌
        updateItemUI(1, binding.ivCheckTarawih, binding.tvStatusTarawih, binding.tvTitleTarawih)
        updateItemUI(2, binding.ivCheckWitir, binding.tvStatusWitir, binding.tvTitleWitir)
        updateItemUI(3, binding.ivCheckDhuha, binding.tvStatusDhuha, binding.tvTitleDhuha)
        updateItemUI(4, binding.ivCheckTahajud, binding.tvStatusTahajud, binding.tvTitleTahajud)
        updateItemUI(5, binding.ivCheckSedekah, binding.tvStatusSedekah, binding.tvTitleSedekah)

        // Hitung Progress
        val totalSelesai = sunnahStatus.values.count { it }
        val totalTarget = 5
        
        binding.tvProgressCount.text = String.format(Locale.US, "%d/%d Selesai", totalSelesai, totalTarget)
        binding.progressIndicator.progress = (totalSelesai.toFloat() / totalTarget * 100).toInt()
        
        binding.tvProgressMsg.text = when {
            totalSelesai == totalTarget -> "Masya Allah, sempurna amalan sunnahmu!"
            totalSelesai > 2 -> "Bagus, tingkatkan lagi amalanmu!"
            else -> "Ayo perbanyak amalan sunnah hari ini!"
        }
    }

    private fun updateItemUI(idKategori: Int, imageView: ImageView, statusView: TextView, titleView: TextView) {
        val isDone = sunnahStatus[idKategori] ?: false
        
        if (isDone) {
            imageView.setImageResource(R.drawable.ic_checked_circle)
            statusView.visibility = View.VISIBLE
            titleView.setTypeface(null, android.graphics.Typeface.BOLD)
        } else {
            imageView.setImageResource(R.drawable.ic_unchecked_circle)
            statusView.visibility = View.GONE
            titleView.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    private fun simpanProgress() {
        val selectedIds = sunnahStatus.filter { it.value }.keys.toList()
        
        binding.loadingBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val success = controller.saveIbadahSunnah(selectedIds)
                binding.loadingBar.visibility = View.GONE
                if (success) {
                    Toast.makeText(this@IbadahSunnahActivity, "Amalan sunnah berhasil disimpan! ✅", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    Toast.makeText(this@IbadahSunnahActivity, "Gagal menyimpan progress", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.loadingBar.visibility = View.GONE
                Log.e("IbadahSunnah", "Error simpanProgress: ${e.message}")
                Toast.makeText(this@IbadahSunnahActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
