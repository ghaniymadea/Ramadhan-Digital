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
import com.pemula.ramadhandigital.utils.DateHelper
import kotlinx.coroutines.launch
import java.util.*

class IbadahSunnahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIbadahSunnahBinding
    private val controller = IbadahSunnahController()
    
    // Map untuk menyimpan status checklist (ID Kategori sesuai database 🍌)
    private val sunnahStatus = mutableMapOf<Int, Boolean>()
    private var isAlreadySaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIbadahSunnahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SessionManager(this).syncToAccount()
        setupToolbar()
        setupSwipeRefresh()
        setupClickListeners()
        loadData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        
        binding.tvDate.text = DateHelper.getTodayDisplay()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadData()
        }
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
                
                var anyEntryFound = false
                dataList?.forEach { ibadah ->
                    // Ambil status: Prioritaskan sudahDilakukan, tapi jika ada ID ibadah sunnah != 0, anggap SELESAI 🕵️‍♂️
                    val isDone = ibadah.sudahDilakukan || ibadah.idIbadahSunnah != 0 || ibadah.id != 0
                    
                    val catId = if (ibadah.idKategoriSunnah != 0) ibadah.idKategoriSunnah else 0
                    
                    if (catId in 1..5 && isDone) {
                        sunnahStatus[catId] = true
                        anyEntryFound = true
                    }
                    
                    // Cek juga di detail (jika ada)
                    ibadah.detailIbadahSunnahs?.forEach { detail ->
                        val dCatId = detail.idKategoriSunnah
                        if (dCatId in 1..5 && detail.sudahDilakukan) {
                            sunnahStatus[dCatId] = true
                            anyEntryFound = true
                        }
                    }
                }
                
                // Jika ada data dari server (bukan list kosong), kunci halaman agar tidak double simpan 🔐
                isAlreadySaved = anyEntryFound || !dataList.isNullOrEmpty() 
                binding.swipeRefresh.isRefreshing = false
                updateUI()
                updateUI()
            } catch (e: Exception) {
                binding.loadingBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
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

        // KUNCI UI JIKA SUDAH SIMPAN 🔐
        if (isAlreadySaved) {
            binding.btnSimpan.visibility = View.GONE
            // Kunci semua baris agar tidak bisa diklik
            arrayOf(binding.rowTarawih, binding.rowWitir, binding.rowDhuha, binding.rowTahajud, binding.rowSedekah).forEach {
                it.root.isEnabled = false
            }
        } else {
            binding.btnSimpan.visibility = View.VISIBLE
            arrayOf(binding.rowTarawih, binding.rowWitir, binding.rowDhuha, binding.rowTahajud, binding.rowSedekah).forEach {
                it.root.isEnabled = true
            }
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
