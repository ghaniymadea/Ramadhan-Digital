package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.SetoranHafalanAdapter
import com.pemula.ramadhandigital.adapter.TrackingSiswaAdapter
import com.pemula.ramadhandigital.controller.AbsensiController
import com.pemula.ramadhandigital.controller.BacaanSholatController
import com.pemula.ramadhandigital.controller.SetoranHafalanController
import com.pemula.ramadhandigital.controller.SurahController
import com.pemula.ramadhandigital.databinding.ActivityMonitoringSetoranBinding
import com.pemula.ramadhandigital.model.*
import com.pemula.ramadhandigital.utils.DateHelper
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class MonitoringSetoranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonitoringSetoranBinding
    private val setoranController = SetoranHafalanController()
    private val absensiController = AbsensiController()
    private val surahController = SurahController()
    private val bacaanController = BacaanSholatController()

    private var allStudents = listOf<AbsensiItem>()
    private var isViewingDetail = false
    private var selectedSiswaId: Int? = null
    private var selectedSiswaNama: String? = null
    private var setoranType: String = "SURAH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitoringSetoranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SessionManager(this).syncToAccount()
        setoranType = intent.getStringExtra("TYPE") ?: "SURAH"

        setupToolbar()
        setupBackHandler()
        setupSearch()
        setupSwipeRefresh()
        loadDaftarSiswa()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            if (isViewingDetail) {
                selectedSiswaId?.let { loadDetailSetoran(it) }
            } else {
                loadDaftarSiswa()
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        updateTitle()
        binding.toolbar.setNavigationOnClickListener { handleBackAction() }
    }

    private fun updateTitle() {
        supportActionBar?.title = if (isViewingDetail) {
            "Setoran: $selectedSiswaNama"
        } else {
            if (setoranType == "SURAH") "Monitor Setoran Surah" else "Monitor Setoran Bacaan"
        }
    }

    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackAction()
            }
        })
    }

    private fun handleBackAction() {
        if (isViewingDetail) {
            showStudentList()
        } else {
            finish()
        }
    }

    private fun loadDaftarSiswa() {
        binding.progressBar.visibility = View.VISIBLE
        binding.cardSearch.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val today = DateHelper.getTodayApi()
                // Mengambil daftar siswa berdasarkan kelas Guru 👨‍🏫
                val data = absensiController.getAbsensi(Account.IdKelas, today) ?: listOf()
                allStudents = data
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                displayStudentList(allStudents)
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(this@MonitoringSetoranActivity, "Gagal memuat daftar siswa", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayStudentList(list: List<AbsensiItem>) {
        isViewingDetail = false
        updateTitle()
        if (list.isEmpty()) {
            showEmptyState("Siswa tidak ditemukan")
            return
        }

        binding.layoutEmptyState.visibility = View.GONE
        binding.rvMonitoring.visibility = View.VISIBLE
        
        // Map data agar bisa pakai TrackingSiswaAdapter (Card Nama) 🛠️
        val mappedList = list.map { 
            IbadahHarian(idUser = it.idUser, namaUser = it.namaSiswa) 
        }

        val adapter = TrackingSiswaAdapter(mappedList) { item ->
            selectedSiswaId = item.idUser
            selectedSiswaNama = item.namaUser
            loadDetailSetoran(item.idUser)
        }
        binding.rvMonitoring.layoutManager = LinearLayoutManager(this)
        binding.rvMonitoring.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchSiswa.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!isViewingDetail) {
                    val filtered = allStudents.filter { 
                        it.namaSiswa.contains(newText ?: "", ignoreCase = true) 
                    }
                    displayStudentList(filtered)
                }
                return true
            }
        })
    }

    private fun loadDetailSetoran(idUser: Int) {
        isViewingDetail = true
        updateTitle()
        binding.progressBar.visibility = View.VISIBLE
        binding.rvMonitoring.visibility = View.GONE
        binding.layoutEmptyState.visibility = View.GONE
        binding.cardSearch.visibility = View.GONE 

        lifecycleScope.launch {
            try {
                // Load data setoran spesifik milik siswa menggunakan endpoint per UserID 🚀
                val dataJob = async { setoranController.getSetoranSiswa(idUser, setoranType) }
                val surahJob = async { if (setoranType == "SURAH") surahController.getJuzAmma() else null }
                val bacaanJob = async { if (setoranType == "BACAAN_SHOLAT") bacaanController.getBacaanSholat() else null }

                val rawHistory = dataJob.await() ?: listOf()
                val masterSurah = surahJob.await() ?: listOf()
                val masterBacaan = bacaanJob.await() ?: listOf()

                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                
                val finalData = mutableListOf<SetoranHafalan>()

                if (setoranType == "SURAH") {
                    masterSurah.forEach { surah ->
                        val match = rawHistory.filter { it.idSurah == surah.id }
                            .sortedBy { it.idStatusSetoranHafalan }
                            .firstOrNull()

                        if (match != null) {
                            finalData.add(match.copy(surah = surah))
                        } else {
                            finalData.add(SetoranHafalan(
                                id = 0,
                                idUser = idUser,
                                idSurah = surah.id,
                                idBacaanSholat = null,
                                idStatusSetoranHafalan = 2,
                                note = null,
                                tanggalSetoran = null,
                                surah = surah
                            ))
                        }
                    }
                } else {
                    masterBacaan.forEach { bacaan ->
                        val match = rawHistory.filter { it.idBacaanSholat == bacaan.id }
                            .sortedBy { it.idStatusSetoranHafalan }
                            .firstOrNull()

                        if (match != null) {
                            finalData.add(match.copy(bacaanSholat = bacaan))
                        } else {
                            finalData.add(SetoranHafalan(
                                id = 0,
                                idUser = idUser,
                                idSurah = null,
                                idBacaanSholat = bacaan.id,
                                idStatusSetoranHafalan = 2,
                                note = null,
                                tanggalSetoran = null,
                                bacaanSholat = bacaan
                            ))
                        }
                    }
                }

                if (finalData.isNotEmpty()) {
                    val adapter = SetoranHafalanAdapter(finalData) { item ->
                        if (item.id != 0) {
                            val intent = Intent(this@MonitoringSetoranActivity, DetailSetoranActivity::class.java).apply {
                                putExtra("ITEM_JSON", Gson().toJson(item))
                            }
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@MonitoringSetoranActivity, "Siswa ini belum menyetorkan materi tersebut.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    binding.rvMonitoring.layoutManager = LinearLayoutManager(this@MonitoringSetoranActivity)
                    binding.rvMonitoring.adapter = adapter
                    binding.rvMonitoring.visibility = View.VISIBLE
                } else {
                    showEmptyState("Belum ada data setoran.")
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@MonitoringSetoranActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showStudentList() {
        isViewingDetail = false
        binding.cardSearch.visibility = View.VISIBLE
        updateTitle()
        displayStudentList(allStudents)
    }

    private fun showEmptyState(msg: String) {
        binding.rvMonitoring.visibility = View.GONE
        binding.layoutEmptyState.visibility = View.VISIBLE
        binding.tvEmptyDesc.text = msg
    }
}
