package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
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
import com.pemula.ramadhandigital.databinding.ActivityIbadahHarianBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.Kegiatan
import com.pemula.ramadhandigital.model.KegiatanUser
import kotlinx.coroutines.launch

class IbadahHarianActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIbadahHarianBinding
    private val controller = IbadahHarianController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIbadahHarianBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadData()

        binding.fabAdd.setOnClickListener {
            showInputDialog()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Catatan Ibadah Harian"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val data = controller.getIbadahHarianHariIni()
                binding.progressBar.visibility = View.GONE
                
                if (data != null) {
                    val list = listOf(
                        KegiatanUser(
                            id = data.id,
                            idUser = data.idUser,
                            idKegiatan = 0,
                            note = "",
                            user = null,
                            kegiatan = Kegiatan(
                                id = 0,
                                judul = "Ibadah Harian - ${data.tanggal}",
                                pemateri = "Target: ${data.targetBacaan ?: "-"}",
                                tanggal = data.tanggal,
                                kegiatanUsers = null,
                                jam = if (data.membacaAlquran) "Al-Quran: Sudah" else "Al-Quran: Belum"
                            )
                        )
                    )
                    
                    val adapter = KegiatanUserAdapter(list) {
                        Toast.makeText(this@IbadahHarianActivity, "Data terkunci", Toast.LENGTH_SHORT).show()
                    }
                    binding.rvIbadah.layoutManager = LinearLayoutManager(this@IbadahHarianActivity)
                    binding.rvIbadah.adapter = adapter
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Input Ibadah Hari Ini")
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 40, 60, 10)
        }

        val cbQuran = CheckBox(this).apply { text = "Membaca Al-Quran" }
        val etTarget = EditText(this).apply { hint = "Target (Contoh: Juz 30)" }

        layout.addView(cbQuran)
        layout.addView(etTarget)
        builder.setView(layout)

        builder.setPositiveButton("Simpan") { _, _ ->
            simpanKeServer(etTarget.text.toString(), cbQuran.isChecked)
        }
        builder.setNegativeButton("Batal", null).show()
    }

    private fun simpanKeServer(target: String, baca: Boolean) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            if (controller.registerIbadahHarian(target, baca)) {
                Toast.makeText(this@IbadahHarianActivity, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
                loadData()
            } else {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@IbadahHarianActivity, "Gagal simpan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
