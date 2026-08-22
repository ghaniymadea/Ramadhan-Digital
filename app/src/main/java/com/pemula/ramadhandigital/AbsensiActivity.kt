package com.pemula.ramadhandigital

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.AbsensiAdapter
import com.pemula.ramadhandigital.controller.AbsensiController
import com.pemula.ramadhandigital.databinding.ActivityAbsensiBinding
import com.pemula.ramadhandigital.model.AbsensiItem
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.PostAbsensiItem
import com.pemula.ramadhandigital.model.PostAbsensiRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AbsensiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsensiBinding
    private lateinit var sessionManager: SessionManager
    private val controller = AbsensiController()

    private var listSiswaFull: List<AbsensiItem>? = null
    private var adapter: AbsensiAdapter? = null
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsensiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SINKRONISASI SESSION 🔐
        sessionManager = SessionManager(this)
        sessionManager.syncToAccount()

        setupToolbar()

        // Tanggal Otomatis (yyyy-MM-dd)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        selectedDate = sdf.format(Date())

        // Ambil Data Tanpa Filter Ketat agar Data Muncul Kembali 🚀
        loadAbsensi(Account.IdKelas, selectedDate)

        // Fitur Pencarian
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterSiswa(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnSimpan.setOnClickListener {
            simpanAbsensi(selectedDate)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadAbsensi(idKelas: Int, tanggal: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // Ambil data asli dari server
                val rawData = controller.getAbsensi(idKelas, tanggal)

                if (rawData != null) {
                    // KEMBALIKAN DATA: Tampilkan semua tanpa filter yang merusak list
                    listSiswaFull = rawData

                    if (listSiswaFull!!.isNotEmpty()) {
                        adapter = AbsensiAdapter(listSiswaFull!!)
                        binding.rvAbsensi.layoutManager = LinearLayoutManager(this@AbsensiActivity)
                        binding.rvAbsensi.adapter = adapter
                    } else {
                        Toast.makeText(this@AbsensiActivity, "Daftar kosong dari server", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AbsensiActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("AbsensiActivity", "Error: ${e.message}")
                Toast.makeText(this@AbsensiActivity, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun filterSiswa(query: String) {
        val filtered = listSiswaFull?.filter {
            it.namaSiswa.contains(query, ignoreCase = true)
        }
        adapter?.updateData(filtered ?: emptyList())
    }

    private fun simpanAbsensi(tanggal: String) {
        val currentList = listSiswaFull ?: return
        binding.progressBar.visibility = View.VISIBLE

        val items = currentList.map {
            PostAbsensiItem(it.idUser, it.idStatusAbsensi ?: 1)
        }

        val request = PostAbsensiRequest(
            tanggal = "${tanggal}T00:00:00Z",
            siswaList = items
        )

        lifecycleScope.launch {
            try {
                if (controller.simpanAbsensi(request)) {
                    Toast.makeText(this@AbsensiActivity, "Absensi disimpan! ✅", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AbsensiActivity, "Gagal simpan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AbsensiActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}
