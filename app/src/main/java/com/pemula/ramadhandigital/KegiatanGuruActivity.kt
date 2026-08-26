package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.KegiatanUserAdapter
import com.pemula.ramadhandigital.controller.KegiatanUserController
import com.pemula.ramadhandigital.databinding.ActivityKegiatanBinding
import com.pemula.ramadhandigital.model.KegiatanUser
import kotlinx.coroutines.launch

/**
 * Halaman Tracking Detail untuk Guru 🧐
 * Menggunakan endpoint: GET /api/v1/kegiatan/user/{idUser}
 */
class KegiatanGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKegiatanBinding
    private val controller = KegiatanUserController()
    private var idUser: Int = -1
    private var namaSiswa: String = "Siswa"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKegiatanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data kiriman dari TrackingSiswaActivity 📦
        idUser = intent.getIntExtra("ID_USER", -1)
        namaSiswa = intent.getStringExtra("NAMA_SISWA") ?: "Siswa"

        setupToolbar()
        setupUI()
        loadData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Tracking: $namaSiswa"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupUI() {
        // Sesuai endpoint yang tersedia untuk Guru (GET /user/{idUser}),
        // fitur tambah master (FAB) dinonaktifkan karena fokus pada pemantauan.
        binding.fabAdd.visibility = View.GONE
    }

    private fun loadData() {
        if (idUser == -1) {
            Toast.makeText(this, "Data siswa tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // Tembak endpoint Guru: GET /api/v1/kegiatan/user/{idUser} 🍌🚀
                val logs = controller.getKegiatanUser(idUser)
                binding.progressBar.visibility = View.GONE

                if (!logs.isNullOrEmpty()) {
                    // Gunakan adapter untuk menampilkan riwayat kegiatan siswa
                    val adapter = KegiatanUserAdapter(logs, isGuruMode = false) { item ->
                        showDetailDialog(item)
                    }
                    binding.rvKegiatan.layoutManager = LinearLayoutManager(this@KegiatanGuruActivity)
                    binding.rvKegiatan.adapter = adapter
                } else {
                    Toast.makeText(this@KegiatanGuruActivity, "Siswa belum mengisi kegiatan apapun.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@KegiatanGuruActivity, "Gagal memuat riwayat kegiatan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDetailDialog(item: KegiatanUser) {
        val note = item.note ?: "(Belum ada catatan)"
        val ustadz = item.kegiatan?.pemateri ?: "-"
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(item.kegiatan?.judul ?: "Kegiatan")
            .setMessage("Pemateri: $ustadz\n\nCatatan Siswa:\n$note")
            .setPositiveButton("Tutup", null)
            .show()
    }
}
