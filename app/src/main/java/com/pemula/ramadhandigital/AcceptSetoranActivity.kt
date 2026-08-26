package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.AcceptSetoranAdapter
import com.pemula.ramadhandigital.controller.SetoranHafalanController
import com.pemula.ramadhandigital.databinding.ActivityAcceptSetoranBinding
import com.pemula.ramadhandigital.model.SetoranHafalan
import kotlinx.coroutines.launch

class AcceptSetoranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAcceptSetoranBinding
    private val controller = SetoranHafalanController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcceptSetoranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadSetoranData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Verifikasi Hafalan"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadSetoranData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // Perbaikan: Gunakan getDaftarSetoran() sesuai Controller 🍌🐒
                val listSetoran = controller.getDaftarSetoran()
                binding.progressBar.visibility = View.GONE

                if (listSetoran != null && listSetoran.isNotEmpty()) {
                    val adapter = AcceptSetoranAdapter(listSetoran) { setoran ->
                        // Guru meng-accept hafalan santri 🔥
                        // Kirim objek lengkap dengan ID Status 1 (Diterima/Lancar)
                        updateStatus(setoran, 1)
                    }
                    binding.rvSetoran.layoutManager = LinearLayoutManager(this@AcceptSetoranActivity)
                    binding.rvSetoran.adapter = adapter
                } else {
                    Toast.makeText(this@AcceptSetoranActivity, "Belum ada setoran yang masuk", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@AcceptSetoranActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStatus(setoran: SetoranHafalan, idStatusBaru: Int) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            // Buat copy objek dengan status baru 🚀
            val setoranUpdate = setoran.copy(idStatusSetoranHafalan = idStatusBaru)

            val sukses = controller.updateStatusSetoran(setoran.id, setoranUpdate)
            binding.progressBar.visibility = View.GONE

            if (sukses) {
                Toast.makeText(this@AcceptSetoranActivity, "Hafalan Berhasil Diverifikasi! ✅", Toast.LENGTH_SHORT).show()
                loadSetoranData() // Refresh list
            } else {
                Toast.makeText(this@AcceptSetoranActivity, "Gagal memverifikasi hafalan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
