package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pemula.ramadhandigital.controller.TausiahController
import com.pemula.ramadhandigital.databinding.ActivityAddTausiahBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.Tausiah
import com.pemula.ramadhandigital.utils.DateHelper
import kotlinx.coroutines.launch
import java.util.*

class AddTausiahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTausiahBinding
    private val controller = TausiahController()

    private var tausiahId: Int = -1
    private var isSubmitted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTausiahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tausiahId = intent.getIntExtra("TAUSIAH_ID", -1)
        val judulLama = intent.getStringExtra("TAUSIAH_JUDUL") ?: ""
        val ustadzLama = intent.getStringExtra("TAUSIAH_USTADZ") ?: ""
        val ringkasanLama = intent.getStringExtra("TAUSIAH_RINGKASAN") ?: ""
        val tanggalLama = intent.getStringExtra("TAUSIAH_TANGGAL") ?: ""
        isSubmitted = intent.getBooleanExtra("TAUSIAH_SUBMITTED", false)

        setupToolbar()
        setupUI(judulLama, ustadzLama, ringkasanLama, tanggalLama)

        binding.btnSubmit.setOnClickListener {
            val j = binding.etJudul.text.toString().trim()
            val p = binding.etPenceramah.text.toString().trim()
            val r = binding.etRingkasan.text.toString().trim()

            if (j.isEmpty() || p.isEmpty() || r.isEmpty()) {
                Toast.makeText(this, "Wajib diisi semua ya Bos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmDialog(j, p, r)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupUI(judul: String, ustadz: String, ringkasan: String, tanggal: String) {
        if (tanggal.isNotEmpty()) {
            binding.tvTanggal.text = "Tanggal: ${DateHelper.stripTime(tanggal)}"
        } else {
            binding.tvTanggal.text = "Tanggal: ${DateHelper.getTodayApi()}"
        }

        binding.etJudul.setText(judul)
        binding.etPenceramah.setText(ustadz)
        binding.etRingkasan.setText(ringkasan)

        // LOGIKA KUNCI: Sembunyikan tombol & kunci input jika sudah diisi 🕵️‍♂️👦
        if (Account.isGuru()) {
            binding.etJudul.isEnabled = false
            binding.etPenceramah.isEnabled = false
            binding.etRingkasan.isEnabled = false
            binding.layoutAction.visibility = View.GONE
            binding.tvStatusLocked.visibility = View.VISIBLE
            binding.tvStatusLocked.text = "🔒 Mode Monitoring: Guru sedang memantau catatan ini."
            supportActionBar?.title = "Catatan Siswa"
        } else {
            // Logika untuk Siswa (👦): Kunci jika sudah submit atau ada isinya
            val hasContent = judul.trim().isNotEmpty() || ringkasan.trim().isNotEmpty()
            
            if (isSubmitted || tausiahId > 0 || hasContent) {
                // 1. Hilangkan tombol simpan 🚫
                binding.etJudul.isEnabled = false
                binding.etPenceramah.isEnabled = false
                binding.etRingkasan.isEnabled = false
                binding.layoutAction.visibility = View.GONE
                
                // 2. Tampilkan status terkunci agar bisa dilihat saja 📖
                binding.tvStatusLocked.visibility = View.VISIBLE
                binding.tvStatusLocked.text = "✅ Catatan ini sudah kamu kirim & kunci."
                supportActionBar?.title = "Catatan Tausiah (Terkunci)"
            } else {
                // Mode Tulis: Belum ada catatan
                binding.etJudul.isEnabled = true
                binding.etPenceramah.isEnabled = true
                binding.etRingkasan.isEnabled = true
                binding.layoutAction.visibility = View.VISIBLE
                binding.tvStatusLocked.visibility = View.GONE
                supportActionBar?.title = "Tulis Catatan Tausiah"
            }
        }
    }

    private fun showConfirmDialog(judul: String, penceramah: String, ringkasan: String) {
        AlertDialog.Builder(this)
            .setTitle("Kirim Catatan")
            .setMessage("Setelah disubmit, catatan ini akan dikunci. Kirim sekarang?")
            .setPositiveButton("Ya, Kirim") { _, _ ->
                simpanKeServer(judul, penceramah, ringkasan)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun simpanKeServer(j: String, p: String, r: String) {
        binding.btnSubmit.isEnabled = false
        lifecycleScope.launch {
            try {
                val data = Tausiah(
                    id = if (tausiahId <= 0) 0 else tausiahId,
                    idUser = Account.Id,
                    tanggal = DateHelper.getTodayApi(),
                    judulTausiah = j,
                    namaPenceramah = p,
                    ringkasan = r,
                    isSubmitted = true 
                )

                if (controller.saveTausiah(data)) {
                    Toast.makeText(this@AddTausiahActivity, "Alhamdulillah, sudah terkirim!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    binding.btnSubmit.isEnabled = true
                    Toast.makeText(this@AddTausiahActivity, "Gagal simpan ke server.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.btnSubmit.isEnabled = true
                Toast.makeText(this@AddTausiahActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
