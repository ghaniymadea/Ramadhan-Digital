package com.pemula.ramadhandigital

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.SurahAdapter
import com.pemula.ramadhandigital.controller.SurahController
import com.pemula.ramadhandigital.databinding.ActivityJuzAmmaBinding
import com.pemula.ramadhandigital.model.Surah
import kotlinx.coroutines.launch

class JuzAmmaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJuzAmmaBinding
    private val controller = SurahController()

    // Variabel untuk menampung adapter dan list data asli
    private var adapter: SurahAdapter? = null
    private var listSurahAsli: List<Surah> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJuzAmmaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSearchView()
        loadJuzAmma()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    // Menata tampilan & listener SearchView
    private fun setupSearchView() {
        // Styling teks di dalam SearchView (opsional)
        val searchEditText = binding.svSurah.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText?.apply {
            setTextColor(Color.parseColor("#1F2937"))
            setHintTextColor(Color.parseColor("#9CA3AF"))
            textSize = 14f
        }

        // Listener saat kata kunci diketik
        binding.svSurah.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterSurah(newText)
                return true
            }
        })
    }

    private fun loadJuzAmma() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            val listSurah = controller.getJuzAmma()
            binding.progressBar.visibility = View.GONE

            if (listSurah != null) {
                listSurahAsli = listSurah // Simpan data mentah asli

                adapter = SurahAdapter(listSurah) { surah ->
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

    // Fungsi memfilter daftar surah
    private fun filterSurah(query: String?) {
        val listFiltered = ArrayList<Surah>()

        if (query.isNullOrEmpty()) {
            listFiltered.addAll(listSurahAsli)
        } else {
            val filterPattern = query.lowercase().trim()
            for (item in listSurahAsli) {
                // Pencarian berdasarkan Nama Surah atau Arti Surah dengan Safe Call 🍌🐒
                val nama = item.surahName?.lowercase() ?: ""
                val arti = item.artiSurat?.lowercase() ?: ""
                
                if (nama.contains(filterPattern) || arti.contains(filterPattern)) {
                    listFiltered.add(item)
                }
            }
        }

        // Monyet panggil fungsi updateList yang tadi kita buat di Adapter! 🍌✨
        adapter?.updateList(listFiltered)
    }
}
