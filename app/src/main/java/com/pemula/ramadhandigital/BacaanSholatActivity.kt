package com.pemula.ramadhandigital

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.BacaanSholatAdapter
import com.pemula.ramadhandigital.controller.BacaanSholatController
import com.pemula.ramadhandigital.databinding.ActivityBacaanSholatBinding
import kotlinx.coroutines.launch

class BacaanSholatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBacaanSholatBinding
    private val controller = BacaanSholatController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Monyet hubungin kodenya sama desain layarnya 🍌
        binding = ActivityBacaanSholatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadBacaanSholat()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadBacaanSholat() {
        // Monyet munculin tanda muter-muter pas lagi nunggu kiriman pisang 🐒
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                // Minta daftar bacaan sholat ke asisten controller
                val listBacaan = controller.getBacaanSholat()
                binding.progressBar.visibility = View.GONE
                
                if (listBacaan != null && listBacaan.isNotEmpty()) {
                    // Monyet pasang sopir truk (Adapter) buat bawa datanya ke layar
                    val adapter = BacaanSholatAdapter(listBacaan)
                    binding.rvBacaanSholat.layoutManager = LinearLayoutManager(this@BacaanSholatActivity)
                    binding.rvBacaanSholat.adapter = adapter
                } else {
                    Toast.makeText(this@BacaanSholatActivity, "Waduh, datanya belum ada Bos!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@BacaanSholatActivity, "Gagal dapet data: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}