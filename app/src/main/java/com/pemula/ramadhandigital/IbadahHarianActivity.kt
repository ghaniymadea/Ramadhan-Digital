package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pemula.ramadhandigital.controller.IbadahHarianController
import com.pemula.ramadhandigital.databinding.ActivityIbadahHarianBinding
import com.pemula.ramadhandigital.model.DetailSholatWajib
import com.pemula.ramadhandigital.model.IbadahHarian
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class IbadahHarianActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIbadahHarianBinding
    private val controller = IbadahHarianController()
    
    private var currentData: IbadahHarian = IbadahHarian(tanggal = getCurrentDate())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIbadahHarianBinding.inflate(layoutInflater)
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
        
        binding.tvDate.text = getFormattedDate()
    }

    private fun setupClickListeners() {
        binding.itemSubuh.setOnClickListener { toggleSholat("Subuh") }
        binding.itemDzuhur.setOnClickListener { toggleSholat("Dzuhur") }
        binding.itemAshar.setOnClickListener { toggleSholat("Ashar") }
        binding.itemMaghrib.setOnClickListener { toggleSholat("Maghrib") }
        binding.itemIsya.setOnClickListener { toggleSholat("Isya") }
        
        binding.ivCheckQuran.setOnClickListener { 
            currentData = currentData.copy(membacaAlquran = !currentData.membacaAlquran)
            updateUI()
        }

        binding.btnSimpan.setOnClickListener {
            simpanProgress()
        }
    }

    private fun toggleSholat(kategori: String) {
        val currentList = currentData.detailSholatWajibs?.toMutableList() ?: mutableListOf()
        val existingIndex = currentList.indexOfFirst { it.kategori.equals(kategori, ignoreCase = true) }
        
        if (existingIndex != -1) {
            val existing = currentList[existingIndex]
            if (existing.status == "Berjamaah di Masjid") {
                currentList[existingIndex] = existing.copy(status = "Belum", idStatusSholatWajib = 0)
            } else {
                currentList[existingIndex] = existing.copy(status = "Berjamaah di Masjid", idStatusSholatWajib = 1)
            }
        } else {
            currentList.add(DetailSholatWajib(
                kategori = kategori,
                status = "Berjamaah di Masjid",
                idStatusSholatWajib = 1
            ))
        }
        
        currentData = currentData.copy(detailSholatWajibs = currentList)
        updateUI()
    }

    private fun loadData() {
        binding.loadingBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val data = controller.getIbadahHarianHariIni()
                binding.loadingBar.visibility = View.GONE
                if (data != null) {
                    currentData = data
                }
                updateUI()
            } catch (e: Exception) {
                binding.loadingBar.visibility = View.GONE
                Toast.makeText(this@IbadahHarianActivity, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        updateSholatUI("Subuh", binding.ivCheckSubuh, binding.tvStatusSubuh, binding.tvTitleSubuh)
        updateSholatUI("Dzuhur", binding.ivCheckDzuhur, binding.tvStatusDzuhur, binding.tvTitleDzuhur)
        updateSholatUI("Ashar", binding.ivCheckAshar, binding.tvStatusAshar, binding.tvTitleAshar)
        updateSholatUI("Maghrib", binding.ivCheckMaghrib, binding.tvStatusMaghrib, binding.tvTitleMaghrib)
        updateSholatUI("Isya", binding.ivCheckIsya, binding.tvStatusIsya, binding.tvTitleIsya)
        
        if (currentData.membacaAlquran) {
            binding.ivCheckQuran.setImageResource(R.drawable.ic_checked_circle)
        } else {
            binding.ivCheckQuran.setImageResource(R.drawable.ic_unchecked_circle)
        }

        binding.tvTargetQuran.text = currentData.targetBacaan ?: "Belum ada target"

        // Calculate progress
        var totalSelesai = 0
        currentData.detailSholatWajibs?.forEach { 
            if (it.status == "Berjamaah di Masjid") totalSelesai++
        }
        if (currentData.membacaAlquran) totalSelesai++

        val totalTarget = 6
        binding.tvProgressCount.text = String.format(Locale.getDefault(), "%d/%d Selesai", totalSelesai, totalTarget)
        binding.progressIndicator.progress = (totalSelesai.toFloat() / totalTarget * 100).toInt()
        
        binding.tvProgressMsg.text = when {
            totalSelesai == totalTarget -> "Masya Allah, selesai semua!"
            totalSelesai > 3 -> "Alhamdulillah, hampir selesai!"
            else -> "Ayo semangat ibadahnya!"
        }
    }

    private fun updateSholatUI(kategori: String, imageView: ImageView, statusView: TextView, titleView: TextView) {
        val detail = currentData.detailSholatWajibs?.find { it.kategori.equals(kategori, ignoreCase = true) }
        val isDone = detail?.status == "Berjamaah di Masjid"
        
        if (isDone) {
            imageView.setImageResource(R.drawable.ic_checked_circle)
            statusView.visibility = View.VISIBLE
            statusView.text = detail?.status
            statusView.setTextColor(android.graphics.Color.parseColor("#004D40"))
            titleView.setTypeface(null, android.graphics.Typeface.BOLD)
        } else {
            imageView.setImageResource(R.drawable.ic_unchecked_circle)
            if (detail?.status == "Belum") {
                statusView.visibility = View.VISIBLE
                statusView.text = "Belum"
                statusView.setTextColor(android.graphics.Color.parseColor("#B91C1C"))
            } else {
                statusView.visibility = View.GONE
            }
            titleView.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    private fun simpanProgress() {
        binding.loadingBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val success = controller.registerIbadahHarian(currentData)
                binding.loadingBar.visibility = View.GONE
                if (success) {
                    Toast.makeText(this@IbadahHarianActivity, "Progress berhasil disimpan!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@IbadahHarianActivity, "Gagal menyimpan progress", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.loadingBar.visibility = View.GONE
                Toast.makeText(this@IbadahHarianActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getFormattedDate(): String {
        return "12 Ramadhan 1445 H" 
    }
}
