package com.pemula.ramadhandigital

import android.R
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
import com.pemula.ramadhandigital.model.*
import com.pemula.ramadhandigital.utils.DateHelper
import com.google.gson.Gson
import kotlinx.coroutines.launch
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
    private var selectedSurahId: Int? = null
    private var selectedBacaanId: Int? = null
    private var selectedStatusId: Int = 1 // Default: Tuntas
    private var setoranType: String = "SURAH"
    
    private var isEditMode = false
    private var editItemId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSetoranGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setoranType = intent.getStringExtra("TYPE") ?: "SURAH"
        isEditMode = intent.getBooleanExtra("EDIT_MODE", false)

        setupToolbar()
        setupUIByType()
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
        val title = if (isEditMode) {
            if (setoranType == "SURAH") "Update Setoran Surah" else "Update Setoran Bacaan"
        } else {
            if (setoranType == "SURAH") "Input Setoran Surah" else "Input Setoran Bacaan Sholat"
        }
        supportActionBar?.title = title
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupUIByType() {
        if (setoranType == "SURAH") {
            binding.tvLabelSurah.visibility = View.VISIBLE
            binding.tilSurah.visibility = View.VISIBLE
            binding.tvLabelBacaan.visibility = View.GONE
            binding.tilBacaan.visibility = View.GONE
        } else {
            binding.tvLabelSurah.visibility = View.GONE
            binding.tilSurah.visibility = View.GONE
            binding.tvLabelBacaan.visibility = View.VISIBLE
            binding.tilBacaan.visibility = View.VISIBLE
        }
    }

    private fun loadInitialData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val idKelasInt = Account.IdKelas
                val today = DateHelper.getTodayApi()

                listSiswa = absensiController.getAbsensi(idKelasInt, today) ?: listOf()
                
                if (setoranType == "SURAH") {
                    listSurah = surahController.getJuzAmma() ?: listOf()
                } else {
                    listBacaan = bacaanController.getBacaanSholat() ?: listOf()
                }

                binding.progressBar.visibility = View.GONE
                setupSpinners()
                
                if (isEditMode) {
                    preFillData()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@AddSetoranGuruActivity, "Gagal memuat data pendukung", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun preFillData() {
        val json = intent.getStringExtra("ITEM_JSON")
        val item = try { Gson().fromJson(json, SetoranHafalan::class.java) } catch (e: Exception) { null }
        
        if (item != null) {
            editItemId = item.id
            selectedSiswaId = item.idUser
            selectedSurahId = item.idSurah
            selectedBacaanId = item.idBacaanSholat
            selectedStatusId = item.idStatusSetoranHafalan

            // Siswa
            val siswa = listSiswa.find { it.idUser == item.idUser }
            binding.spinnerSiswa.setText(siswa?.namaSiswa ?: "", false)
            
            // Surah / Bacaan
            if (setoranType == "SURAH") {
                val surah = listSurah.find { it.id == item.idSurah }
                binding.spinnerSurah.setText(surah?.surahName ?: "", false)
            } else {
                val bacaan = listBacaan.find { it.id == item.idBacaanSholat }
                binding.spinnerBacaan.setText(bacaan?.nama ?: "", false)
            }

            // Status
            val statusText = if (selectedStatusId == 1) "Tuntas" else "Belum Tuntas"
            binding.spinnerStatus.setText(statusText, false)

            // Catatan
            binding.etCatatan.setText(item.note ?: "")

            // Tanggal
            if (!item.tanggalSetoran.isNullOrEmpty()) {
                binding.etTanggal.setText(DateHelper.toDisplayDate(item.tanggalSetoran))
            }
        }
    }

    private fun setupSpinners() {
        val adapterSiswa = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listSiswa.map { it.namaSiswa })
        binding.spinnerSiswa.setAdapter(adapterSiswa)
        binding.spinnerSiswa.setOnItemClickListener { _, _, _, _ ->
            // Cari siswa berdasarkan nama yang dipilih untuk mendukung pencarian/filter 🔍
            val selectedName = binding.spinnerSiswa.text.toString()
            val siswa = listSiswa.find { it.namaSiswa == selectedName }
            selectedSiswaId = siswa?.idUser ?: -1
        }

        if (setoranType == "SURAH") {
            val adapterSurah = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listSurah.map { it.surahName ?: "" })
            binding.spinnerSurah.setAdapter(adapterSurah)
            binding.spinnerSurah.setOnItemClickListener { _, _, _, _ ->
                val selectedName = binding.spinnerSurah.text.toString()
                val surah = listSurah.find { it.surahName == selectedName }
                selectedSurahId = surah?.id
            }
        } else {
            val adapterBacaan = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, listBacaan.map { it.nama ?: "" })
            binding.spinnerBacaan.setAdapter(adapterBacaan)
            binding.spinnerBacaan.setOnItemClickListener { _, _, _, _ ->
                val selectedName = binding.spinnerBacaan.text.toString()
                val bacaan = listBacaan.find { it.nama == selectedName }
                selectedBacaanId = bacaan?.id
            }
        }
    }

    private fun setupStatusSpinner() {
        val statuses = listOf("Tuntas", "Belum Tuntas")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statuses)
        binding.spinnerStatus.setAdapter(adapter)
        binding.spinnerStatus.setText(statuses[0], false)
        binding.spinnerStatus.setOnItemClickListener { _, _, position, _ ->
            selectedStatusId = position + 1
        }
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        binding.etTanggal.setText(DateHelper.getTodayDisplay())

        binding.etTanggal.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                calendar.set(year, month, day)
                binding.etTanggal.setText(DateHelper.toDisplayDate(DateHelper.toApiDate(calendar.time)))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun validateAndSave() {
        val note = binding.etCatatan.text.toString().trim()
        val tanggalStr = binding.etTanggal.text.toString()

        if (selectedSiswaId == -1) {
            Toast.makeText(this, "Pilih siswa dulu ya!", Toast.LENGTH_SHORT).show()
            return
        }

        if (setoranType == "SURAH" && selectedSurahId == null) {
            Toast.makeText(this, "Pilih surah dulu ya!", Toast.LENGTH_SHORT).show()
            return
        }

        if (setoranType == "BACAAN_SHOLAT" && selectedBacaanId == null) {
            Toast.makeText(this, "Pilih bacaan sholat dulu ya!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val formattedDate = DateHelper.fromDisplayToApi(tanggalStr)

            val dataSetoran = SetoranHafalan(
                id = editItemId,
                idUser = selectedSiswaId,
                idSurah = selectedSurahId,
                idBacaanSholat = selectedBacaanId,
                idStatusSetoranHafalan = selectedStatusId,
                note = note,
                tanggalSetoran = formattedDate
            )

            val sukses = if (isEditMode) {
                setoranController.updateStatusSetoran(editItemId, dataSetoran, setoranType)
            } else {
                setoranController.simpanSetoran(dataSetoran, setoranType)
            }

            binding.progressBar.visibility = View.GONE
            if (sukses) {
                val msg = if (isEditMode) "Setoran berhasil diupdate! ✅" else "Setoran berhasil disimpan! ✅"
                Toast.makeText(this@AddSetoranGuruActivity, msg, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@AddSetoranGuruActivity, "Gagal menyimpan ke server.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
