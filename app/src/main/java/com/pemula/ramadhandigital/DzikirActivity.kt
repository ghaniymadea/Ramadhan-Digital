package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.DzikirAdapter
import com.pemula.ramadhandigital.controller.DzikirController
import com.pemula.ramadhandigital.databinding.ActivityDzikirBinding
import kotlinx.coroutines.launch

class DzikirActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDzikirBinding
    private val controller = DzikirController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDzikirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadDzikir()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadDzikir() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val listDzikir = controller.getDzikir()
                binding.progressBar.visibility = View.GONE
                
                if (listDzikir != null && listDzikir.isNotEmpty()) {
                    val adapter = DzikirAdapter(listDzikir)
                    binding.rvDzikir.layoutManager = LinearLayoutManager(this@DzikirActivity)
                    binding.rvDzikir.adapter = adapter
                } else {
                    Toast.makeText(this@DzikirActivity, "Data dzikir kosong", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@DzikirActivity, "Gagal mengambil data: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}