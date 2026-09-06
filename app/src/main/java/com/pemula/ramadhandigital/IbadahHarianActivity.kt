package com.pemula.ramadhandigital

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pemula.ramadhandigital.controller.IbadahHarianController
import com.pemula.ramadhandigital.databinding.ActivityIbadahHarianBinding
import com.pemula.ramadhandigital.model.DetailSholatWajib
import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.utils.DateHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class IbadahHarianActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIbadahHarianBinding
    private val controller = IbadahHarianController()
    
    private var currentData: IbadahHarian = IbadahHarian(tanggal = DateHelper.getTodayApi())
    
    private val statusOptions = arrayOf("Pilih Status", "Berjamaah di Masjid", "Munfarid (Sendiri)", "Tidak Sholat")
    private val statusIds = intArrayOf(0, 1, 2, 3)

    private val sholatMappping = mapOf(
        "Subuh" to 1, "Dzuhur" to 2, "Ashar" to 3, "Maghrib" to 4, "Isya" to 5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIbadahHarianBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SessionManager(this).syncToAccount()
        setupToolbar()
        setupSwipeRefresh()
        setupSpinners()
        setupClickListeners()
        
        // Load data hari ini secara default 🚀
        val today = DateHelper.getTodayApi()
        loadData(today)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            val dateToLoad = currentData.tanggal ?: DateHelper.getTodayApi()
            loadData(dateToLoad)
        }
    }

    private fun setupSpinners() {
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
                    val newIdStatus = statusIds[position]
                    val katId = sholatMappping[kategoris[index]] ?: 0
                    val currentDetail = currentData.detailSholatWajibs?.find { it.idKategoriSholatWajib == katId }
                    
                    updateSpinnerStyle(spinner, newIdStatus)

                    if (currentDetail?.idStatusSholatWajib != newIdStatus) {
                        updateSholatData(kategoris[index], katId, statusOptions[position], newIdStatus)
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
        // Tekan tanggal untuk ganti hari 📅✨
        binding.tvDate.setOnClickListener { showDatePicker() }

        binding.ivCheckSubuh.setOnClickListener { toggleMasjid("Subuh", 1) }
        binding.ivCheckDzuhur.setOnClickListener { toggleMasjid("Dzuhur", 2) }
        binding.ivCheckAshar.setOnClickListener { toggleMasjid("Ashar", 3) }
        binding.ivCheckMaghrib.setOnClickListener { toggleMasjid("Maghrib", 4) }
        binding.ivCheckIsya.setOnClickListener { toggleMasjid("Isya", 5) }

        binding.ivCheckQuran.setOnClickListener { 
            currentData = currentData.copy(membacaAlquran = !currentData.membacaAlquran)
            updateUI()
            if (currentData.membacaAlquran) binding.etTargetQuran.requestFocus()
        }

        binding.btnSimpan.setOnClickListener { simpanProgress() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        // Jika sedang melihat tanggal tertentu, buka picker di tanggal itu
        try {
            val dateStr = DateHelper.stripTime(currentData.tanggal)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = sdf.parse(dateStr ?: "")
            if (date != null) calendar.time = date
        } catch (e: Exception) {}

        val picker = DatePickerDialog(this, { _, year, month, day ->
            calendar.set(year, month, day)
            val dateStr = DateHelper.toApiDate(calendar.time)
            loadData(dateStr) // Ambil data untuk tanggal terpilih 🚀
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        
        picker.show()
    }

    private fun loadData(tanggal: String) {
        binding.loadingBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // Panggil fungsi controller dengan query tanggal 🐒📊
                val data = controller.getIbadahHarianByDate(tanggal)
                binding.loadingBar.visibility = View.GONE
                
                if (data != null) {
                    currentData = data
                } else {
                    // Jika data 404 (belum diisi), buat data baru kosong untuk tanggal tersebut
                    currentData = IbadahHarian(tanggal = tanggal, detailSholatWajibs = emptyList())
                }
                binding.swipeRefresh.isRefreshing = false
                updateUI()
            } catch (e: Exception) {
                binding.loadingBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(this@IbadahHarianActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        // Update teks tanggal di header
        binding.tvDate.text = DateHelper.toDisplayDate(currentData.tanggal)

        val spinners = arrayOf(binding.spStatusSubuh, binding.spStatusDzuhur, binding.spStatusAshar, binding.spStatusMaghrib, binding.spStatusIsya)
        val images = arrayOf(binding.ivCheckSubuh, binding.ivCheckDzuhur, binding.ivCheckAshar, binding.ivCheckMaghrib, binding.ivCheckIsya)
        val titles = arrayOf(binding.tvTitleSubuh, binding.tvTitleDzuhur, binding.tvTitleAshar, binding.tvTitleMaghrib, binding.tvTitleIsya)
        val kategoris = arrayOf("Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya")

        var totalSelesai = 0

        kategoris.forEachIndexed { i, katName ->
            val katId = sholatMappping[katName] ?: 0
            val detail = currentData.detailSholatWajibs?.find { it.idKategoriSholatWajib == katId }
            val idStatus = detail?.idStatusSholatWajib ?: 0
            
            val targetPos = statusIds.indexOf(idStatus).coerceAtLeast(0)
            if (spinners[i].selectedItemPosition != targetPos) {
                spinners[i].setSelection(targetPos, false)
            }
            updateSpinnerStyle(spinners[i], idStatus)
            
            if (idStatus == 1 || idStatus == 2) {
                images[i].setImageResource(R.drawable.ic_checked_circle)
                titles[i].setTypeface(null, android.graphics.Typeface.BOLD)
                totalSelesai++
            } else {
                images[i].setImageResource(R.drawable.ic_unchecked_circle)
                titles[i].setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }
        
        binding.ivCheckQuran.setImageResource(if (currentData.membacaAlquran) R.drawable.ic_checked_circle else R.drawable.ic_unchecked_circle)
        binding.etTargetQuran.isEnabled = currentData.membacaAlquran
        binding.etTargetQuran.setText(currentData.targetBacaan ?: "")
        if (currentData.membacaAlquran) totalSelesai++

        val totalTarget = 6
        binding.tvProgressCount.text = "$totalSelesai/$totalTarget Selesai"
        binding.progressIndicator.progress = (totalSelesai.toFloat() / totalTarget * 100).toInt()
        binding.tvProgressMsg.text = if (totalSelesai == totalTarget) "Masya Allah, sempurna!" else "Ayo semangat ibadahnya!"

        // SEMBUNYIKAN TOMBOL JIKA SUDAH ISI 🔐
        val hasSholat = currentData.detailSholatWajibs?.any { it.idStatusSholatWajib != 0 } == true
        val hasQuran = currentData.membacaAlquran
        val isFilled = currentData.sudahMengisi || hasSholat || hasQuran
        
        if (isFilled) {
            binding.btnSimpan.visibility = View.GONE
            binding.etTargetQuran.isEnabled = false
            // Spinners juga dikunci agar tidak bisa diubah setelah simpan
            arrayOf(binding.spStatusSubuh, binding.spStatusDzuhur, binding.spStatusAshar, binding.spStatusMaghrib, binding.spStatusIsya).forEach {
                it.isEnabled = false
            }
            arrayOf(binding.ivCheckSubuh, binding.ivCheckDzuhur, binding.ivCheckAshar, binding.ivCheckMaghrib, binding.ivCheckIsya, binding.ivCheckQuran).forEach {
                it.isEnabled = false
            }
        } else {
            binding.btnSimpan.visibility = View.VISIBLE
            binding.etTargetQuran.isEnabled = currentData.membacaAlquran
            arrayOf(binding.spStatusSubuh, binding.spStatusDzuhur, binding.spStatusAshar, binding.spStatusMaghrib, binding.spStatusIsya).forEach {
                it.isEnabled = true
            }
            arrayOf(binding.ivCheckSubuh, binding.ivCheckDzuhur, binding.ivCheckAshar, binding.ivCheckMaghrib, binding.ivCheckIsya, binding.ivCheckQuran).forEach {
                it.isEnabled = true
            }
        }
    }

    private fun updateSholatData(kategori: String, idKategori: Int, status: String, idStatus: Int) {
        val currentList = currentData.detailSholatWajibs?.toMutableList() ?: mutableListOf()
        val existingIndex = currentList.indexOfFirst { it.idKategoriSholatWajib == idKategori }
        
        if (existingIndex != -1) {
            currentList[existingIndex] = currentList[existingIndex].copy(status = status, idStatusSholatWajib = idStatus)
        } else {
            currentList.add(DetailSholatWajib(idKategoriSholatWajib = idKategori, kategori = kategori, status = status, idStatusSholatWajib = idStatus))
        }
        currentData = currentData.copy(detailSholatWajibs = currentList)
        updateUI()
    }

    private fun toggleMasjid(kategori: String, idKategori: Int) {
        val detail = currentData.detailSholatWajibs?.find { it.idKategoriSholatWajib == idKategori }
        val isAlreadyMasjid = detail?.idStatusSholatWajib == 1
        updateSholatData(kategori, idKategori, if (isAlreadyMasjid) "Pilih Status" else "Berjamaah di Masjid", if (isAlreadyMasjid) 0 else 1)
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
                    Toast.makeText(this@IbadahHarianActivity, "Progress disimpan! ✅", Toast.LENGTH_SHORT).show()
                    loadData(currentData.tanggal ?: DateHelper.getTodayApi())
                } else Toast.makeText(this@IbadahHarianActivity, "Gagal simpan", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                binding.loadingBar.visibility = View.GONE
                Toast.makeText(this@IbadahHarianActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
