package com.pemula.ramadhandigital

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.core.graphics.toColorInt
import com.pemula.ramadhandigital.controller.KegiatanUserController
import com.pemula.ramadhandigital.databinding.ActivityAddKegiatanBinding
import com.pemula.ramadhandigital.model.Account
import kotlinx.coroutines.launch

class AddKegiatanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddKegiatanBinding
    private val controller = KegiatanUserController()
    
    private var idKegiatan: Int = -1
    private var isSubmitted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddKegiatanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Tangkap kiriman data 📦
        idKegiatan = intent.getIntExtra("ID_KEGIATAN", -1)
        val judul = intent.getStringExtra("JUDUL") ?: ""
        val ustadz = intent.getStringExtra("USTADZ") ?: ""
        val note = intent.getStringExtra("NOTE") ?: ""
        isSubmitted = intent.getBooleanExtra("IS_SUBMITTED", false)

        setupToolbar()
        setupUI(judul, ustadz, note)

        binding.btnSubmit.setOnClickListener {
            val content = binding.etNote.text.toString().trim()
            if (content.isNotEmpty()) {
                simpanKeServer(content)
            } else {
                Toast.makeText(this, "Tulis catatannya dulu ya!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupUI(judul: String, ustadz: String, note: String) {
        binding.tvJudulKegiatan.text = judul
        binding.tvUstadz.text = "Pemateri: $ustadz"
        binding.etNote.setText(note)

        // LOGIKA KUNCI: Sembunyikan tombol & kunci input jika sudah diisi 🕵️‍♂️👦
        // Kunci jika: 1. User adalah Guru, 2. Flag isSubmitted true, 3. Isi catatan (note) tidak kosong
        val isLocked = Account.isGuru() || isSubmitted || note.trim().isNotEmpty()

        if (isLocked) {
            // 1. Sembunyikan tombol simpan agar tidak muncul lagi 🚫
            binding.btnSubmit.visibility = View.GONE
            
            // 2. Kunci input catatan (Read Only) agar siswa bisa lihat tapi tidak bisa ubah 📖
            binding.etNote.isEnabled = false
            binding.etNote.isFocusable = false
            binding.etNote.setTextColor(Color.parseColor("#1E293B")) // Warna gelap agar mudah dibaca
            
            binding.tvStatusLocked.visibility = View.VISIBLE
            
            if (Account.isGuru()) {
                binding.tvStatusLocked.text = "👀 Mode Lihat: Guru sedang memantau catatan ini."
                supportActionBar?.title = "Detail Catatan Siswa"
            } else {
                binding.tvStatusLocked.text = "✅ Catatan ini sudah kamu simpan & terkunci."
                supportActionBar?.title = "Catatan Kegiatan (Terkunci)"
            }
        } else {
            // Mode Isi: Jika belum ada catatan, tampilkan tombol & buka input ✍️
            binding.etNote.isEnabled = true
            binding.etNote.isFocusable = true
            binding.etNote.isFocusableInTouchMode = true
            binding.btnSubmit.visibility = View.VISIBLE
            binding.tvStatusLocked.visibility = View.GONE
            supportActionBar?.title = "Tulis Catatan Kegiatan"
        }
    }

    private fun simpanKeServer(note: String) {
        binding.btnSubmit.isEnabled = false
        lifecycleScope.launch {
            try {
                // Tembak API simpan kegiatan pesantren 🚀🔥
                val sukses = controller.registerKegiatan(Account.Id, idKegiatan, note)
                if (sukses) {
                    Toast.makeText(this@AddKegiatanActivity, "Alhamdulillah, catatan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    binding.btnSubmit.isEnabled = true
                    Toast.makeText(this@AddKegiatanActivity, "Waduh, gagal simpan ke server.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.btnSubmit.isEnabled = true
                Toast.makeText(this@AddKegiatanActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
