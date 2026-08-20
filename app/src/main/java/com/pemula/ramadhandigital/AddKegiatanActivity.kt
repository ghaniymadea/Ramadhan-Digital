package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

        // LOGIKA KUNCI: Kalau sudah pernah isi, gembok tulisannya! 🔐🍌
        if (isSubmitted) {
            binding.etNote.isEnabled = false
            binding.btnSubmit.visibility = View.GONE
            binding.tvStatusLocked.visibility = View.VISIBLE
            supportActionBar?.title = "Detail Kegiatan (Terkunci)"
        } else {
            binding.etNote.isEnabled = true
            binding.btnSubmit.visibility = View.VISIBLE
            binding.tvStatusLocked.visibility = View.GONE
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
