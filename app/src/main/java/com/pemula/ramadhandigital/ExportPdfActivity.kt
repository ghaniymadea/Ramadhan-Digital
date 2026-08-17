package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pemula.ramadhandigital.databinding.ActivityExportPdfBinding

class ExportPdfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExportPdfBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExportPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        binding.btnExport.setOnClickListener {
            generatePdf()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun generatePdf() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnExport.isEnabled = false
        
        // Logika pembuatan PDF di sini 🍌📄
        // Bisa menembak API backend yang menghasilkan PDF atau menggunakan library lokal
        
        binding.root.postDelayed({
            binding.progressBar.visibility = View.GONE
            binding.btnExport.isEnabled = true
            Toast.makeText(this, "Laporan PDF berhasil diunduh!", Toast.LENGTH_LONG).show()
        }, 2000)
    }
}
