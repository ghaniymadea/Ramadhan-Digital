package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.TrackingSiswaAdapter
import com.pemula.ramadhandigital.controller.IbadahHarianController
import com.pemula.ramadhandigital.databinding.ActivityTrackingSiswaBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.utils.DateHelper
import kotlinx.coroutines.launch
import java.util.*

class TrackingSiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackingSiswaBinding
    private val ibadahController = IbadahHarianController()
    
    private var listSiswaFull: List<IbadahHarian>? = null
    private var adapter: TrackingSiswaAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingSiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SessionManager(this).syncToAccount()
        setupToolbar()
        setupSwipeRefresh()
        setupSearch()
        loadSiswaData()
    }

    private fun setupSearch() {
        binding.svSiswa.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterSiswa(newText ?: "")
                return true
            }
        })
    }

    private fun filterSiswa(query: String) {
        val filtered = listSiswaFull?.filter {
            it.namaUser?.contains(query, ignoreCase = true) == true
        }
        if (filtered != null) {
            updateRecyclerView(filtered)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadSiswaData()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Hapus setDisplayShowTitleEnabled(false) agar judul muncul 🚀
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadSiswaData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val currentDate = DateHelper.getTodayApi()
                val idKelasInt = Account.IdKelas

                val listIbadah = ibadahController.getMonitoringKelas(idKelasInt, currentDate)
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false

                if (listIbadah != null) {
                    listSiswaFull = listIbadah
                    updateRecyclerView(listIbadah)
                } else {
                    Toast.makeText(this@TrackingSiswaActivity, "Data tidak ditemukan (404/Empty)", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                Log.e("TrackingSiswa", "Error: ${e.message}")
            }
        }
    }

    private fun updateRecyclerView(list: List<IbadahHarian>) {
        adapter = TrackingSiswaAdapter(list) { item ->
            // Buka DetailKegiatanSiswaActivity agar Guru bisa melihat catatan kegiatan 🕵️‍♂️
            val intent = Intent(this@TrackingSiswaActivity, DetailKegiatanSiswaActivity::class.java)
            intent.putExtra("ID_USER", if (item.idUser != 0) item.idUser else item.id)
            intent.putExtra("NAMA_SISWA", item.namaUser)
            startActivity(intent)
        }
        binding.rvTracking.layoutManager = LinearLayoutManager(this@TrackingSiswaActivity)
        binding.rvTracking.adapter = adapter
    }
}
