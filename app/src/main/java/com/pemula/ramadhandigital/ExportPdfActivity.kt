package com.pemula.ramadhandigital

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pemula.ramadhandigital.databinding.ActivityExportPdfBinding
import com.pemula.ramadhandigital.model.Account

class ExportPdfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExportPdfBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExportPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        binding.btnExport.setOnClickListener {
            // AMBIL ID KELAS LANGSUNG DARI ACCOUNT 🍌🚀
            val idKelas = Account.IdKelas
            exportData(idKelas)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun exportData(idKelas: Int) {
        Toast.makeText(this, "Mengekspor data kelas ID: $idKelas", Toast.LENGTH_SHORT).show()
        // Logika export PDF...
    }
}
