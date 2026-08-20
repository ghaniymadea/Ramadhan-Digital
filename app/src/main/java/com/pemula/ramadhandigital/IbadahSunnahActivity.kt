package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.KegiatanUserAdapter
import com.pemula.ramadhandigital.controller.IbadahSunnahController
import com.pemula.ramadhandigital.databinding.ActivityIbadahSunnahBinding
import com.pemula.ramadhandigital.model.Kegiatan
import com.pemula.ramadhandigital.model.KegiatanUser
import kotlinx.coroutines.launch

class IbadahSunnahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIbadahSunnahBinding
    private val controller = IbadahSunnahController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIbadahSunnahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadData()

        binding.fabAdd.setOnClickListener {
            showSunnahDialog()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Catatan Ibadah Sunnah"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // Ambil data sunnah hari ini dari server 🍌🐒
                val data = controller.getMyIbadahSunnahHariIni()
                binding.progressBar.visibility = View.GONE
                
                val list = data?.map {
                    KegiatanUser(
                        id = it.id,
                        idUser = it.idUser,
                        idKegiatan = 0,
                        note = "",
                        user = null,
                        kegiatan = Kegiatan(
                            id = 0,
                            judul = it.kategori?.nama ?: "Ibadah Sunnah",
                            pemateri = "Mandiri",
                            tanggal = it.tanggal,
                            kegiatanUsers = null,
                            jam = "Sudah Dikerjakan"
                        )
                    )
                }

                if (!list.isNullOrEmpty()) {
                    val adapter = KegiatanUserAdapter(list) {
                        Toast.makeText(this@IbadahSunnahActivity, "Ibadah sudah tercatat", Toast.LENGTH_SHORT).show()
                    }
                    binding.rvIbadah.layoutManager = LinearLayoutManager(this@IbadahSunnahActivity)
                    binding.rvIbadah.adapter = adapter
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showSunnahDialog() {
        val names = arrayOf("Sholat Tahajud", "Sholat Dhuha", "Sholat Witir", "Sholat Rawatib", "Sedekah")
        val ids = intArrayOf(1, 2, 3, 4, 5)
        val checked = BooleanArray(names.size)

        AlertDialog.Builder(this)
            .setTitle("Pilih Ibadah Sunnah Hari Ini")
            .setMultiChoiceItems(names, checked) { _, which, isChecked ->
                checked[which] = isChecked
            }
            .setPositiveButton("Simpan") { _, _ ->
                val selected = mutableListOf<Int>()
                for (i in checked.indices) {
                    if (checked[i]) selected.add(ids[i])
                }
                if (selected.isNotEmpty()) simpanSunnah(selected)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun simpanSunnah(ids: List<Int>) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            if (controller.saveIbadahSunnah(ids)) {
                Toast.makeText(this@IbadahSunnahActivity, "Berhasil simpan!", Toast.LENGTH_SHORT).show()
                loadData()
            } else {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@IbadahSunnahActivity, "Gagal simpan ke server", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
