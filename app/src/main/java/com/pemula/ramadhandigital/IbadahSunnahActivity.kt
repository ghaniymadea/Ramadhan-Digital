package com.pemula.ramadhandigital

import android.os.Bundle
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
    
    // Map untuk menyimpan status checklist (ID Kategori -> Boolean) 🍌
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
        
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        binding.tvDate.text = sdf.format(Date())
    }

    private fun setupClickListeners() {
        binding.itemTahajud.setOnClickListener { toggleSunnah(1) }
        binding.itemDhuha.setOnClickListener { toggleSunnah(2) }
        binding.itemWitir.setOnClickListener { toggleSunnah(3) }
        binding.itemRawatib.setOnClickListener { toggleSunnah(4) }
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
                // Ambil data sunnah hari ini 🍌🐒
                val dataList = controller.getMyIbadahSunnahHariIni()
                binding.loadingBar.visibility = View.GONE
                
                // Reset status
                sunnahStatus.clear()
                
                // Isi status dari data server
                dataList?.forEach { ibadah ->
                    ibadah.detailIbadahSunnahs?.forEach { detail ->
                        if (detail.idKategoriIbadahSunnah != 0) {
                            sunnahStatus[detail.idKategoriIbadahSunnah] = true
                        }
                    }
                }
                
                updateUI()
            } catch (e: Exception) {
                binding.loadingBar.visibility = View.GONE
                Toast.makeText(this@IbadahSunnahActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        // Update masing-masing item UI 🍌
        updateItemUI(1, binding.ivCheckTahajud, binding.tvStatusTahajud, binding.tvTitleTahajud)
        updateItemUI(2, binding.ivCheckDhuha, binding.tvStatusDhuha, binding.tvTitleDhuha)
        updateItemUI(3, binding.ivCheckWitir, binding.tvStatusWitir, binding.tvTitleWitir)
        updateItemUI(4, binding.ivCheckRawatib, binding.tvStatusRawatib, binding.tvTitleRawatib)
        updateItemUI(5, binding.ivCheckSedekah, binding.tvStatusSedekah, binding.tvTitleSedekah)

        // Hitung Progress
        val totalSelesai = sunnahStatus.values.count { it }
        val totalTarget = 5
        
        binding.tvProgressCount.text = "$totalSelesai/$totalTarget Selesai"
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
                    Toast.makeText(this@IbadahSunnahActivity, "Progress sunnah disimpan!", Toast.LENGTH_SHORT).show()
                    loadData() // Refresh data
                } else {
                    Toast.makeText(this@IbadahSunnahActivity, "Gagal menyimpan progress", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.loadingBar.visibility = View.GONE
                Toast.makeText(this@IbadahSunnahActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
