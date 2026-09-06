package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.pemula.ramadhandigital.adapter.MenuAdapter
import com.pemula.ramadhandigital.databinding.ActivityAbsensiMenuBinding
import com.pemula.ramadhandigital.model.MenuItem

class AbsensiMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsensiMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsensiMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SessionManager(this).syncToAccount()
        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pilih Menu Absensi"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        val listMenu = arrayListOf(
            MenuItem(R.drawable.salat, "Input Absensi"),
            MenuItem(R.drawable.ic_calendar, "Rekap Absensi")
        )

        val adapter = MenuAdapter(listMenu) { item ->
            when (item.title) {
                "Input Absensi" -> startActivity(Intent(this, AbsensiActivity::class.java))
                "Rekap Absensi" -> startActivity(Intent(this, RekapAbsensiActivity::class.java))
            }
        }

        binding.rvMenuAbsensi.layoutManager = GridLayoutManager(this, 2)
        binding.rvMenuAbsensi.setHasFixedSize(true)
        binding.rvMenuAbsensi.adapter = adapter
    }
}
