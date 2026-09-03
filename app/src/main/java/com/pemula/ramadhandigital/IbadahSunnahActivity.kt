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
import com.pemula.ramadhandigital.databinding.ItemIbadahSunnahRowBinding
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
        
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID"))
        binding.tvDate.text = sdf.format(Date())
    }

    private fun setupClickListeners() {
        // ID disesuaikan dengan database: Tarawih(1), Witir(2), Dhuha(3), Tahajud(4), Sedekah(5) 🚀
        binding.rowTarawih.root.setOnClickListener { toggleSunnah(1) }
        binding.rowWitir.root.setOnClickListener { toggleSunnah(2) }
        binding.rowDhuha.root.setOnClickListener { toggleSunnah(3) }
        binding.rowTahajud.root.setOnClickListener { toggleSunnah(4) }
        binding.rowSedekah.root.setOnClickListener { toggleSunnah(5) }
        
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
                
                dataList?.forEach { ibadah ->
                    if (ibadah.idKategoriSunnah != 0 && ibadah.sudahDilakukan) {
                        sunnahStatus[ibadah.idKategoriSunnah] = true
                    }
                    
                    ibadah.detailIbadahSunnahs?.forEach { detail ->
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
        updateItemUI(1, binding.rowTarawih, "Sholat Tarawih")
        updateItemUI(2, binding.rowWitir, "Sholat Witir")
        updateItemUI(3, binding.rowDhuha, "Sholat Dhuha")
        updateItemUI(4, binding.rowTahajud, "Sholat Tahajud")
        updateItemUI(5, binding.rowSedekah, "Sedekah Harian")

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

    private fun updateItemUI(idKategori: Int, itemBinding: ItemIbadahSunnahRowBinding, defaultTitle: String) {
        val isDone = sunnahStatus[idKategori] ?: false
        
        itemBinding.tvTitle.text = defaultTitle
        if (isDone) {
            itemBinding.ivCheck.setImageResource(R.drawable.ic_checked_circle)
            itemBinding.tvStatus.visibility = View.VISIBLE
            itemBinding.tvTitle.setTypeface(null, android.graphics.Typeface.BOLD)
        } else {
            itemBinding.ivCheck.setImageResource(R.drawable.ic_unchecked_circle)
            itemBinding.tvStatus.visibility = View.GONE
            itemBinding.tvTitle.setTypeface(null, android.graphics.Typeface.NORMAL)
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
