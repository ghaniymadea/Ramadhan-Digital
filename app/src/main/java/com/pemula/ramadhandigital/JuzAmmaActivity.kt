package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.SurahAdapter
import com.pemula.ramadhandigital.controller.SurahController
import com.pemula.ramadhandigital.databinding.ActivityJuzAmmaBinding
import kotlinx.coroutines.launch

class JuzAmmaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJuzAmmaBinding
    private val controller = SurahController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJuzAmmaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadJuzAmma()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadJuzAmma() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            val listSurah = controller.getJuzAmma()
            binding.progressBar.visibility = View.GONE
            
            if (listSurah != null) {
                val adapter = SurahAdapter(listSurah) { surah ->
                    // MONYET PAKE ID ASLI DARI DATABASE SEKARANG! 🍌🐒
                    val intent = Intent(this@JuzAmmaActivity, DetailSurahActivity::class.java)
                    intent.putExtra("ID_SURAH", surah.id) 
                    intent.putExtra("NAMA_SURAH", surah.surahName)
                    intent.putExtra("TEMPAT_TURUN", surah.tempatTurun)
                    intent.putExtra("ARTI_SURAH", surah.artiSurat)
                    startActivity(intent)
                }
                binding.rvSurah.layoutManager = LinearLayoutManager(this@JuzAmmaActivity)
                binding.rvSurah.adapter = adapter
            } else {
                Toast.makeText(this@JuzAmmaActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
