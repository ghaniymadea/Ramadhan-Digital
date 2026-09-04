package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.pemula.ramadhandigital.adapter.MenuAdapter
import com.pemula.ramadhandigital.databinding.ActivitySetoranHafalanMenuBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.MenuItem

class SetoranHafalanMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetoranHafalanMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetoranHafalanMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pilih Jenis Setoran"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        val listMenu = mutableListOf(
            MenuItem(R.drawable.quran, "Setoran Surah"),
            MenuItem(R.drawable.salat, "Setoran Bacaan Sholat")
        )

        // Tambahkan menu Monitoring jika User adalah GURU 👨‍🏫🚀
        if (Account.isGuru()) {
            listMenu.add(MenuItem(R.drawable.ic_search, "Monitoring Surah"))
            listMenu.add(MenuItem(R.drawable.ic_search, "Monitoring Bacaan"))
        }

        val adapter = MenuAdapter(listMenu) { item ->
            // Tentukan Type berdasarkan judul menu 🔍
            val type = if (item.title.contains("Surah")) "SURAH" else "BACAAN_SHOLAT"
            
            val intent = when {
                // Jika Guru klik menu Monitoring
                item.title.startsWith("Monitoring") -> {
                    Intent(this, MonitoringSetoranActivity::class.java)
                }
                // Jika Guru klik menu Input Biasa
                Account.isGuru() -> {
                    Intent(this, AddSetoranGuruActivity::class.java)
                }
                // Jika Siswa klik
                else -> {
                    Intent(this, SetoranHafalanActivity::class.java)
                }
            }
            
            intent.putExtra("TYPE", type)
            startActivity(intent)
        }

        binding.rvMenuSetoran.layoutManager = GridLayoutManager(this, 2)
        binding.rvMenuSetoran.setHasFixedSize(true)
        binding.rvMenuSetoran.adapter = adapter
    }
}
