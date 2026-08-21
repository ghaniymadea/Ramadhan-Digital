package com.pemula.ramadhandigital

import android.os.Bundle
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
    private var listSiswa: List<AbsensiItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsensiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. SINKRONKAN TOKEN (Wajib agar tidak NULL!) 🔐🐒
        sessionManager = SessionManager(this)
        sessionManager.syncToAccount()

        setupToolbar()

        // 2. Format Tanggal ISO (yyyy-MM-dd) sesuai Backend C# 📅
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val currentDate = sdf.format(Date())
        binding.tvTanggal.text = "Tanggal: $currentDate"

        // 3. MONYET FIX: Ambil ID Kelas sebagai Angka! 🍌🚀
        // Jika Account.Kelas isinya "XII RPL 1", kita ambil angka "1" saja.
        val rawKelas = Account.Kelas ?: "1"
        val idKelasInt = rawKelas.filter { it.isDigit() }.toIntOrNull() ?: 1

        Log.d("AbsensiActivity", "Loading Absensi - Kelas: $idKelasInt, Tanggal: $currentDate")
        loadAbsensi(idKelasInt, currentDate)

        binding.btnSimpan.setOnClickListener {
            simpanAbsensi(currentDate)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadAbsensi(idKelas: Int, tanggal: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                if (Account.Token.isNullOrEmpty()) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@AbsensiActivity, "Token Kosong! Login Ulang.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Ambil data lewat controller
                listSiswa = controller.getAbsensi(idKelas, tanggal)
                binding.progressBar.visibility = View.GONE

                if (listSiswa != null) {
                    val adapter = AbsensiAdapter(listSiswa!!)
                    binding.rvAbsensi.layoutManager = LinearLayoutManager(this@AbsensiActivity)
                    binding.rvAbsensi.adapter = adapter
                    
                    if (listSiswa!!.isEmpty()) {
                        Toast.makeText(this@AbsensiActivity, "Belum ada siswa di kelas ini", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Beri tahu user untuk cek Logcat Android Studio! 🧐
                    Toast.makeText(this@AbsensiActivity, "Gagal memuat data. Cek Koneksi, Token, atau Backend!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Log.e("AbsensiActivity", "Crash Load: ${e.message}")
                Toast.makeText(this@AbsensiActivity, "Terjadi kesalahan koneksi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun simpanAbsensi(tanggal: String) {
        if (listSiswa.isNullOrEmpty()) {
            Toast.makeText(this, "Tidak ada data untuk disimpan", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        
        // 4. Bungkus data sesuai class DetailAbsensiSiswa di C# 🐒
        val items = listSiswa!!.map { 
            PostAbsensiItem(it.idUser, it.idStatusAbsensi ?: 1) // Default 1 (Hadir)
        }
        
        val request = PostAbsensiRequest(
            tanggal = "${tanggal}T00:00:00Z", // Format DateTime ISO
            siswaList = items
        )

        lifecycleScope.launch {
            try {
                val sukses = controller.simpanAbsensi(request)
                binding.progressBar.visibility = View.GONE
                
                if (sukses) {
                    Toast.makeText(this@AbsensiActivity, "Absensi berhasil disimpan! ✅", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AbsensiActivity, "Gagal menyimpan absensi ke server", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@AbsensiActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
