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
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadSetoranData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val listSetoran = controller.getAllSetoran()
                binding.progressBar.visibility = View.GONE
                
                if (listSetoran != null) {
                    val adapter = AcceptSetoranAdapter(listSetoran) { setoran ->
                        // Logika Accept Setoran 🍌🐒
                        updateStatus(setoran.id, 1) // Misal 1 adalah status 'Diterima'
                    }
                    binding.rvSetoran.layoutManager = LinearLayoutManager(this@AcceptSetoranActivity)
                    binding.rvSetoran.adapter = adapter
                } else {
                    Toast.makeText(this@AcceptSetoranActivity, "Data setoran kosong", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@AcceptSetoranActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStatus(idSetoran: Int, idStatus: Int) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val sukses = controller.updateStatusSetoran(idSetoran, idStatus)
            binding.progressBar.visibility = View.GONE
            if (sukses) {
                Toast.makeText(this@AcceptSetoranActivity, "Setoran Berhasil Diterima!", Toast.LENGTH_SHORT).show()
                loadSetoranData() // Refresh data
            } else {
                Toast.makeText(this@AcceptSetoranActivity, "Gagal update status", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
