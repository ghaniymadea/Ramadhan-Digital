package com.pemula.ramadhandigital

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pemula.ramadhandigital.controller.AbsensiController
import com.pemula.ramadhandigital.controller.BacaanSholatController
import com.pemula.ramadhandigital.controller.SetoranHafalanController
import com.pemula.ramadhandigital.controller.SurahController
import com.pemula.ramadhandigital.databinding.ActivityAddSetoranGuruBinding
import com.pemula.ramadhandigital.model.AbsensiItem
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.BacaanSholat
import com.pemula.ramadhandigital.model.Surah
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddSetoranGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddSetoranGuruBinding
    private val setoranController = SetoranHafalanController()
    private val surahController = SurahController()
    private val absensiController = AbsensiController()
    private val bacaanController = BacaanSholatController()

    private var listSiswa = listOf<AbsensiItem>()
    private var listSurah = listOf<Surah>()
    private var listBacaan = listOf<BacaanSholat>()
    
    private var selectedSiswaId: Int = -1
    private var selectedSurahId: Int = -1
    private var selectedBacaanId: Int? = null
    private var selectedStatusId: Int = 1 // Default: Tuntas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSetoranGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadInitialData()
        setupStatusSpinner()
        setupDatePicker()

        binding.btnSimpan.setOnClickListener {
            validateAndSave()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadInitialData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // MONYET FIX: Pastikan idKelas bertipe Int! 🐒🔥
                val rawKelas = Account.Kelas ?: "1"
                val idKelasInt = rawKelas.filter { it.isDigit() }.toIntOrNull() ?: 1
                
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val today = sdf.format(Date())
                
                listSiswa = absensiController.getAbsensi(idKelasInt, today) ?: listOf()
                listSurah = surahController.getJuzAmma() ?: listOf()
                listBacaan = bacaanController.getBacaanSholat() ?: listOf()

                binding.progressBar.visibility = View.GONE
                setupSpinners()
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                e.printStackTrace()
                Toast.makeText(this@AddSetoranGuruActivity, "Gagal memuat data pendukung", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinners() {
        val adapterSiswa = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listSiswa.map { it.namaSiswa })
        binding.spinnerSiswa.setAdapter(adapterSiswa)
        binding.spinnerSiswa.setOnItemClickListener { _, _, position, _ ->
            selectedSiswaId = listSiswa[position].idUser
        }

        val adapterSurah = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listSurah.map { it.surahName ?: "" })
        binding.spinnerSurah.setAdapter(adapterSurah)
        binding.spinnerSurah.setOnItemClickListener { _, _, position, _ ->
            selectedSurahId = listSurah[position].id
        }

        val adapterBacaan = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listBacaan.map { it.nama ?: "" })
        binding.spinnerBacaan.setAdapter(adapterBacaan)
        binding.spinnerBacaan.setOnItemClickListener { _, _, position, _ ->
            selectedBacaanId = listBacaan[position].id
        }
    }

    private fun setupStatusSpinner() {
        val statuses = listOf("Tuntas", "Belum Tuntas")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statuses)
        binding.spinnerStatus.setAdapter(adapter)
        binding.spinnerStatus.setOnItemClickListener { _, _, position, _ ->
            selectedStatusId = position + 1
        }
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val localeId = Locale("in", "ID")
        val sdf = SimpleDateFormat("dd MMMM yyyy", localeId)
        binding.etTanggal.setText(sdf.format(calendar.time))

        binding.etTanggal.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                calendar.set(year, month, day)
                binding.etTanggal.setText(sdf.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun validateAndSave() {
        val note = binding.etCatatan.text.toString().trim()
        val tanggalStr = binding.etTanggal.text.toString()

        if (selectedSiswaId == -1 || selectedSurahId == -1) {
            Toast.makeText(this, "Pilih siswa dan surah dulu ya!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val localeId = Locale("in", "ID")
            val inputSdf = SimpleDateFormat("dd MMMM yyyy", localeId)
            val outputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val formattedDate = try {
                val date = inputSdf.parse(tanggalStr)
                if (date != null) outputSdf.format(date) else outputSdf.format(Date())
            } catch (e: Exception) {
                outputSdf.format(Date())
            }

            val sukses = setoranController.createSetoran(
                idUser = selectedSiswaId,
                idSurah = selectedSurahId,
                idBacaan = selectedBacaanId,
                idStatus = selectedStatusId,
                note = note,
                tanggal = formattedDate
            )

            binding.progressBar.visibility = View.GONE
            if (sukses) {
                Toast.makeText(this@AddSetoranGuruActivity, "Setoran berhasil disimpan! ✅", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@AddSetoranGuruActivity, "Gagal menyimpan ke server.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
