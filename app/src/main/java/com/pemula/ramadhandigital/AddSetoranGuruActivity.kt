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
import com.pemula.ramadhandigital.model.*
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
    private var selectedSurahId: Int? = null
    private var selectedBacaanId: Int? = null
    private var selectedStatusId: Int = 1 // Default: Tuntas
    private var setoranType: String = "SURAH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSetoranGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setoranType = intent.getStringExtra("TYPE") ?: "SURAH"

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
        supportActionBar?.title = if (setoranType == "SURAH") "Input Setoran Surah" else "Input Setoran Bacaan Sholat"
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
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

                listSiswa = absensiController.getAbsensi(idKelasInt, today) ?: listOf()
                
                if (setoranType == "SURAH") {
                    listSurah = surahController.getJuzAmma() ?: listOf()
                } else {
                    listBacaan = bacaanController.getBacaanSholat() ?: listOf()
                }

                binding.progressBar.visibility = View.GONE
                setupSpinners()
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
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

        if (setoranType == "SURAH") {
            val adapterSurah = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listSurah.map { it.surahName ?: "" })
            binding.spinnerSurah.setAdapter(adapterSurah)
            binding.spinnerSurah.setOnItemClickListener { _, _, position, _ ->
                selectedSurahId = listSurah[position].id
            }
        } else {
            val adapterBacaan = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listBacaan.map { it.nama ?: "" })
            binding.spinnerBacaan.setAdapter(adapterBacaan)
            binding.spinnerBacaan.setOnItemClickListener { _, _, position, _ ->
                selectedBacaanId = listBacaan[position].id
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
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
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
            val formattedDate = try {
                val date = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).parse(tanggalStr)
                SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date ?: Date())
            } catch (e: Exception) {
                SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            }

            val dataSetoran = SetoranHafalan(
                id = 0,
                idUser = selectedSiswaId,
                idSurah = selectedSurahId,
                idBacaanSholat = selectedBacaanId,
                idStatusSetoranHafalan = selectedStatusId,
                note = note,
                tanggalSetoran = formattedDate
            )

            // Fix: Tambahkan parameter type (SURAH / BACAAN_SHOLAT) 🚀
            val sukses = setoranController.simpanSetoran(dataSetoran, setoranType)

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
