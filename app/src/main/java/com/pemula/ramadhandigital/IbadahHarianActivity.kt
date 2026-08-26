package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.*
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
    
    // Opsi status sholat sesuai JSON backend 🍌🐒
    private val statusOptions = arrayOf("Pilih Status", "Berjamaah di Masjid", "Munfarid (Sendiri)", "Tidak Sholat")
    private val statusIds = intArrayOf(0, 1, 2, 3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIbadahHarianBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSpinners()
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

    private fun setupSpinners() {
        // Gunakan layout custom biar lebih rapi dan kecil 🐒✨
        val adapter = ArrayAdapter(this, R.layout.item_spinner_status, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinners = arrayOf(
            binding.spStatusSubuh, binding.spStatusDzuhur, binding.spStatusAshar,
            binding.spStatusMaghrib, binding.spStatusIsya
        )
        val kategoris = arrayOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")

        spinners.forEachIndexed { index, spinner ->
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val newId = statusIds[position]
                    val currentDetail = currentData.detailSholatWajibs?.find { it.kategori.equals(kategoris[index], true) }
                    
                    // Update background ala Google Sheets 🐒📊
                    updateSpinnerStyle(spinner, newId)

                    // Update hanya jika berbeda untuk cegah loop 🍌
                    if (currentDetail?.idStatusSholatWajib != newId) {
                        updateSholatData(kategoris[index], statusOptions[position], newId)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun updateSpinnerStyle(spinner: Spinner, idStatus: Int) {
        val bgRes = when (idStatus) {
            1 -> R.drawable.bg_pill_green
            2 -> R.drawable.bg_pill_blue
            3 -> R.drawable.bg_pill_red
            else -> R.drawable.bg_pill_gray
        }
        spinner.setBackgroundResource(bgRes)
    }

    private fun setupClickListeners() {
        // Klik icon centang untuk toggle cepat ke "Berjamaah di Masjid" (ID 1) 🍌
        binding.ivCheckSubuh.setOnClickListener { toggleMasjid("Subuh") }
        binding.ivCheckDzuhur.setOnClickListener { toggleMasjid("Dzuhur") }
        binding.ivCheckAshar.setOnClickListener { toggleMasjid("Ashar") }
        binding.ivCheckMaghrib.setOnClickListener { toggleMasjid("Maghrib") }
        binding.ivCheckIsya.setOnClickListener { toggleMasjid("Isya") }

        binding.ivCheckQuran.setOnClickListener { 
            currentData = currentData.copy(membacaAlquran = !currentData.membacaAlquran)
            updateUI()
            if (currentData.membacaAlquran) binding.etTargetQuran.requestFocus()
        }

        binding.btnSimpan.setOnClickListener {
            simpanProgress()
        }
    }

    private fun updateSholatData(kategori: String, status: String, idStatus: Int) {
        val currentList = currentData.detailSholatWajibs?.toMutableList() ?: mutableListOf()
        val existingIndex = currentList.indexOfFirst { it.kategori.equals(kategori, ignoreCase = true) }
        
        if (existingIndex != -1) {
            currentList[existingIndex] = currentList[existingIndex].copy(status = status, idStatusSholatWajib = idStatus)
        } else {
            currentList.add(DetailSholatWajib(
                kategori = kategori,
                status = status,
                idStatusSholatWajib = idStatus
            ))
        }
        
        currentData = currentData.copy(detailSholatWajibs = currentList)
        updateUI()
    }

    private fun toggleMasjid(kategori: String) {
        val detail = currentData.detailSholatWajibs?.find { it.kategori.equals(kategori, ignoreCase = true) }
        val isAlreadyMasjid = detail?.idStatusSholatWajib == 1
        
        val nextStatus = if (isAlreadyMasjid) "Pilih Status" else "Berjamaah di Masjid"
        val nextId = if (isAlreadyMasjid) 0 else 1
        
        updateSholatData(kategori, nextStatus, nextId)
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
                Toast.makeText(this@IbadahHarianActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        val kategoris = arrayOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")
        val images = arrayOf(binding.ivCheckSubuh, binding.ivCheckDzuhur, binding.ivCheckAshar, binding.ivCheckMaghrib, binding.ivCheckIsya)
        val spinners = arrayOf(binding.spStatusSubuh, binding.spStatusDzuhur, binding.spStatusAshar, binding.spStatusMaghrib, binding.spStatusIsya)
        val titles = arrayOf(binding.tvTitleSubuh, binding.tvTitleDzuhur, binding.tvTitleAshar, binding.tvTitleMaghrib, binding.tvTitleIsya)

        var totalSelesai = 0

        kategoris.forEachIndexed { i, kat ->
            val detail = currentData.detailSholatWajibs?.find { it.kategori.equals(kat, ignoreCase = true) }
            val idStatus = detail?.idStatusSholatWajib ?: 0
            
            // Sync spinner selection (ID -> Position)
            val targetPos = statusIds.indexOf(idStatus).coerceAtLeast(0)
            if (spinners[i].selectedItemPosition != targetPos) {
                spinners[i].setSelection(targetPos, false)
            }

            // Sync style dropdown ala Sheets 🐒📊
            updateSpinnerStyle(spinners[i], idStatus)
            
            if (idStatus == 1 || idStatus == 2) { // Masjid atau Munfarid dianggap selesai 🍌
                images[i].setImageResource(R.drawable.ic_checked_circle)
                titles[i].setTypeface(null, android.graphics.Typeface.BOLD)
                totalSelesai++
            } else {
                images[i].setImageResource(R.drawable.ic_unchecked_circle)
                titles[i].setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }
        
        if (currentData.membacaAlquran) {
            binding.ivCheckQuran.setImageResource(R.drawable.ic_checked_circle)
            binding.etTargetQuran.isEnabled = true
            totalSelesai++
        } else {
            binding.ivCheckQuran.setImageResource(R.drawable.ic_unchecked_circle)
            binding.etTargetQuran.isEnabled = false
        }
        
        if (binding.etTargetQuran.text.isEmpty()) {
            binding.etTargetQuran.setText(currentData.targetBacaan ?: "")
        }

        val totalTarget = 6
        binding.tvProgressCount.text = "$totalSelesai/$totalTarget Selesai"
        binding.progressIndicator.progress = (totalSelesai.toFloat() / totalTarget * 100).toInt()
        
        binding.tvProgressMsg.text = when {
            totalSelesai == totalTarget -> "Masya Allah, sempurna!"
            totalSelesai > 3 -> "Alhamdulillah, sedikit lagi!"
            else -> "Ayo semangat ibadahnya!"
        }
    }

    private fun simpanProgress() {
        val targetInput = binding.etTargetQuran.text.toString().trim()
        currentData = currentData.copy(targetBacaan = targetInput)

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
                Toast.makeText(this@IbadahHarianActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
