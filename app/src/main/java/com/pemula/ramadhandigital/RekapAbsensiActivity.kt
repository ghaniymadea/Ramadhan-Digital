package com.pemula.ramadhandigital

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.RekapAbsensiAdapter
import com.pemula.ramadhandigital.controller.AbsensiController
import com.pemula.ramadhandigital.databinding.ActivityRekapAbsensiBinding
import com.pemula.ramadhandigital.model.AbsensiItem
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.utils.DateHelper
import kotlinx.coroutines.launch
import java.util.*

class RekapAbsensiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRekapAbsensiBinding
    private val controller = AbsensiController()
    private var adapter: RekapAbsensiAdapter? = null
    
    private var isKeseluruhan = false
    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRekapAbsensiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SessionManager(this).syncToAccount()
        
        selectedDate = DateHelper.getTodayApi()
        binding.tvSelectedDate.text = DateHelper.getTodayDisplay()
        
        val idKelas = Account.IdKelas
        if (idKelas != 0) {
            Log.d("RekapAbsensi", "Starting with idKelas: $idKelas")
        } else {
            Toast.makeText(this, "Debug: IdKelas is 0", Toast.LENGTH_SHORT).show()
        }

        setupToolbar()
        setupListeners()
        loadData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener { loadData() }
        
        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isKeseluruhan = (checkedId == R.id.btnKeseluruhan)
                binding.cardDate.visibility = if (isKeseluruhan) View.GONE else View.VISIBLE
                loadData()
            }
        }

        binding.cardDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = DateHelper.toApiDate(calendar.time)
                binding.tvSelectedDate.text = DateHelper.toDisplayDate(selectedDate)
                loadData()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun loadData() {
        val idKelas = Account.IdKelas
        if (idKelas == 0) {
            binding.swipeRefresh.isRefreshing = false
            Toast.makeText(this, "ID Kelas tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        val dateParam = if (isKeseluruhan) null else selectedDate
        
        lifecycleScope.launch {
            try {
                // 1. Ambil DAFTAR SISWA (Roster) - Jangan kirim tanggal agar dapat semua 👦
                val masterSiswa = controller.getAbsensi(idKelas, "") ?: listOf()
                
                // 2. Ambil RIWAYAT REKAP (dari endpoint baru) 📜
                val rawRekap = controller.getRekapAbsensi(idKelas, dateParam) ?: listOf()
                
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false

                val finalDisplayList = mutableListOf<AbsensiItem>()

                if (isKeseluruhan) {
                    // HITUNG REKAP KESELURUHAN DI CLIENT 📊
                    masterSiswa.forEach { siswa ->
                        val riwayatSiswa = rawRekap.filter { it.idUser == siswa.idUser }
                        finalDisplayList.add(siswa.copy(
                            totalHadir = riwayatSiswa.count { it.idStatusAbsensi == 1 },
                            totalIzin = riwayatSiswa.count { it.idStatusAbsensi == 2 },
                            totalSakit = riwayatSiswa.count { it.idStatusAbsensi == 3 },
                            totalAlpa = riwayatSiswa.count { it.idStatusAbsensi == 4 }
                        ))
                    }
                } else {
                    // FILTER HARIAN DI CLIENT 📅
                    masterSiswa.forEach { siswa ->
                        // Cari status siswa untuk tanggal terpilih
                        val match = rawRekap.find { 
                            it.idUser == siswa.idUser && 
                            it.tanggal?.startsWith(selectedDate) == true 
                        }
                        
                        finalDisplayList.add(siswa.copy(
                            idStatusAbsensi = match?.idStatusAbsensi ?: 0,
                            statusAbsensi = when(match?.idStatusAbsensi) {
                                1 -> "Hadir"
                                2 -> "Izin"
                                3 -> "Sakit"
                                4 -> "Alpa"
                                else -> "Belum Absen"
                            }
                        ))
                    }
                }

                if (adapter == null) {
                    adapter = RekapAbsensiAdapter(finalDisplayList, isKeseluruhan)
                    binding.rvRekap.layoutManager = LinearLayoutManager(this@RekapAbsensiActivity)
                    binding.rvRekap.adapter = adapter
                } else {
                    adapter?.updateData(finalDisplayList, isKeseluruhan)
                }
                
            } catch (e: Exception) {
                Log.e("RekapAbsensi", "Error: ${e.message}")
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(this@RekapAbsensiActivity, "Gagal memproses data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
