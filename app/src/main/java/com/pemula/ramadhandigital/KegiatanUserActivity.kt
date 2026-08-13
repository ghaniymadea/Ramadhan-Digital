package com.pemula.ramadhandigital

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.KegiatanUserAdapter
import com.pemula.ramadhandigital.controller.IbadahHarianController
import com.pemula.ramadhandigital.controller.KegiatanUserController
import com.pemula.ramadhandigital.databinding.ActivityKegiatanBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.Kegiatan
import com.pemula.ramadhandigital.model.KegiatanUser
import kotlinx.coroutines.launch

class KegiatanUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKegiatanBinding
    private val kegiatanController = KegiatanUserController()
    private val ibadahController = IbadahHarianController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKegiatanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kategori = intent.getStringExtra("KATEGORI") ?: "Kegiatan Ramadhan"
        setupToolbar(kategori)

        loadKegiatan(kategori)
    }

    private fun setupToolbar(title: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadKegiatan(kategori: String) {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                Log.d("KegiatanUserActivity", "Memulai request untuk kategori: $kategori")

                // PILIH API YANG TEPAT BERDASARKAN KATEGORI 🍌🐒
                val listKegiatanUser = when (kategori) {
                    "CATATAN KEGIATAN PESANTREN RAMADHAN" -> {
                        // PANGGIL MASTER DATA (api/v1/kegiatan)
                        val masterData = kegiatanController.getAllKegiatan()
                        masterData?.map {
                            KegiatanUser(
                                id = 0,
                                idUser = Account.Id,
                                idKegiatan = it.id,
                                note = null,
                                kegiatan = it,
                                user = null
                            )
                        }
                    }
                    "CATATAN APRESIASI IBADAH HARIAN" -> {
                        // PANGGIL DATA IBADAH HARIAN (api/v1/ibadah-harian) 🍌🐒
                        val ibadahData = ibadahController.getIbadahHarian(Account.Id)
                        ibadahData?.map {
                            KegiatanUser(
                                id = it.id,
                                idUser = it.idUser,
                                idKegiatan = 0,
                                note = "Target: ${it.targetBacaan ?: "-"} | Quran: ${if (it.membacaAlquran) "Ya" else "Tidak"}",
                                kegiatan = Kegiatan(
                                    id = 0,
                                    judul = "Ibadah Harian - ${it.tanggal}",
                                    pemateri = "Mandiri",
                                    tanggal = it.tanggal,
                                    kegiatanUsers = null
                                ),
                                user = null
                            )
                        }
                    }
                    else -> {
                        // PANGGIL CATATAN USER (api/v1/kegiatan/user/{id})
                        kegiatanController.getKegiatanUser(Account.Id)
                    }
                }

                binding.progressBar.visibility = View.GONE

                if (listKegiatanUser != null && listKegiatanUser.isNotEmpty()) {
                    val adapter = KegiatanUserAdapter(listKegiatanUser) { item ->
                        if (kategori == "CATATAN APRESIASI IBADAH HARIAN") {
                            showIbadahDialog()
                        } else {
                            showNoteDialog(item.idKegiatan, item.kegiatan?.judul ?: "Kegiatan")
                        }
                    }
                    binding.rvKegiatan.layoutManager = LinearLayoutManager(this@KegiatanUserActivity)
                    binding.rvKegiatan.adapter = adapter
                } else {
                    Log.w("KegiatanUserActivity", "Data kosong dari API")
                    Toast.makeText(this@KegiatanUserActivity, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Log.e("KegiatanUserActivity", "Error Load: ${e.message}")
                Toast.makeText(this@KegiatanUserActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showIbadahDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Input Ibadah Harian")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val cbQuran = CheckBox(this)
        cbQuran.text = "Membaca Al-Quran"

        val etTarget = EditText(this)
        etTarget.hint = "Target Bacaan (Contoh: Juz 30)"

        layout.addView(cbQuran)
        layout.addView(etTarget)
        builder.setView(layout)

        builder.setPositiveButton("Simpan") { _, _ ->
            val target = etTarget.text.toString()
            val membacaAlquran = cbQuran.isChecked
            simpanIbadahKeAPI(target, membacaAlquran)
        }
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun simpanIbadahKeAPI(target: String, membacaAlquran: Boolean) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val sukses = ibadahController.registerIbadahHarian(target, membacaAlquran)
            binding.progressBar.visibility = View.GONE
            if (sukses) {
                Toast.makeText(this@KegiatanUserActivity, "Berhasil simpan ibadah!", Toast.LENGTH_SHORT).show()
                loadKegiatan("CATATAN APRESIASI IBADAH HARIAN")
            } else {
                Toast.makeText(this@KegiatanUserActivity, "Gagal simpan ibadah", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showNoteDialog(idKegiatan: Int, judul: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tambah Catatan")
        builder.setMessage("Tulis kegiatanmu di '$judul'")

        val input = EditText(this)
        input.hint = "Contoh: Sudah ikut tadarus..."

        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(50, 0, 50, 0)
        container.addView(input, params)

        builder.setView(container)
        builder.setPositiveButton("Kirim") { dialog, _ ->
            val note = input.text.toString()
            if (note.isNotEmpty()) simpanKeAPI(idKegiatan, note)
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun simpanKeAPI(idKegiatan: Int, note: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val sukses = kegiatanController.registerKegiatan(Account.Id, idKegiatan, note)
            binding.progressBar.visibility = View.GONE
            if (sukses) {
                Toast.makeText(this@KegiatanUserActivity, "Berhasil simpan!", Toast.LENGTH_SHORT).show()
                loadKegiatan(intent.getStringExtra("KATEGORI") ?: "")
            } else {
                Toast.makeText(this@KegiatanUserActivity, "Gagal simpan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
