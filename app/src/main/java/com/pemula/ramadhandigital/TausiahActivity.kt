package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.TausiahAdapter
import com.pemula.ramadhandigital.adapter.TrackingSiswaAdapter
import com.pemula.ramadhandigital.controller.AbsensiController
import com.pemula.ramadhandigital.controller.TausiahController
import com.pemula.ramadhandigital.databinding.ActivityTausiahBinding
import com.pemula.ramadhandigital.model.AbsensiItem
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.model.Tausiah
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TausiahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTausiahBinding
    private val controller = TausiahController()
    private val absensiController = AbsensiController()

    private var allStudents = listOf<AbsensiItem>()
    private var isViewingTausiahList = false
    private var selectedSiswaId: Int? = null
    private var selectedSiswaNama: String? = null

    private val tausiahLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val idToRefresh = if (Account.isGuru()) selectedSiswaId else Account.getUserIdFromToken()
            idToRefresh?.let { loadTausiahSiswa(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTausiahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SessionManager(this).syncToAccount()
        setupToolbar()
        setupBackHandler()
        setupSwipeRefresh()

        if (Account.isGuru()) {
            setupGuruFlow()
        } else {
            setupSiswaFlow()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { handleBackAction() }
    }

    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackAction()
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            val idToRefresh = if (Account.isGuru() && isViewingTausiahList) selectedSiswaId 
            else if (!Account.isGuru()) Account.getUserIdFromToken() 
            else null

            if (idToRefresh != null) {
                loadTausiahSiswa(idToRefresh)
            } else if (Account.isGuru() && !isViewingTausiahList) {
                loadDaftarSiswa()
            } else {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun handleBackAction() {
        if (Account.isGuru() && isViewingTausiahList) {
            // Jika guru sedang lihat list tausiah siswa, balik ke daftar nama siswa 🔙
            showStudentList()
        } else {
            finish()
        }
    }

    private fun setupGuruFlow() {
        binding.fabAdd.visibility = View.GONE
        binding.cardSearch.visibility = View.VISIBLE
        supportActionBar?.title = "Pilih Siswa"
        
        loadDaftarSiswa()
        setupSearch()
    }

    private fun setupSiswaFlow() {
        binding.fabAdd.visibility = View.VISIBLE
        binding.cardSearch.visibility = View.GONE
        supportActionBar?.title = "Catatan Tausiah Saya"
        
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddTausiahActivity::class.java)
            intent.putExtra("TAUSIAH_ID", 0)
            tausiahLauncher.launch(intent)
        }
        
        loadTausiahSiswa(Account.getUserIdFromToken())
    }

    private fun loadDaftarSiswa() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                // Mengambil daftar siswa di kelas guru 👦
                val data = absensiController.getAbsensi(Account.IdKelas, today) ?: listOf()
                allStudents = data
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                
                displayStudentList(allStudents)
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(this@TausiahActivity, "Gagal memuat daftar siswa", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayStudentList(list: List<AbsensiItem>) {
        isViewingTausiahList = false
        if (list.isEmpty()) {
            showEmptyState("Siswa tidak ditemukan")
            return
        }

        binding.layoutEmptyState.visibility = View.GONE
        binding.rvTausiah.visibility = View.VISIBLE
        
        // Map data ke IbadahHarian agar bisa pakai TrackingSiswaAdapter (Card Nama) 🛠️
        val mappedList = list.map { 
            IbadahHarian(idUser = it.idUser, namaUser = it.namaSiswa) 
        }

        val adapter = TrackingSiswaAdapter(mappedList) { item ->
            selectedSiswaId = item.idUser
            selectedSiswaNama = item.namaUser
            loadTausiahSiswa(item.idUser)
        }
        binding.rvTausiah.layoutManager = LinearLayoutManager(this)
        binding.rvTausiah.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchSiswa.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!isViewingTausiahList) {
                    val filtered = allStudents.filter { 
                        it.namaSiswa.contains(newText ?: "", ignoreCase = true) 
                    }
                    displayStudentList(filtered)
                }
                return true
            }
        })
    }

    private fun loadTausiahSiswa(idUser: Int) {
        isViewingTausiahList = true
        binding.progressBar.visibility = View.VISIBLE
        binding.rvTausiah.visibility = View.GONE
        binding.layoutEmptyState.visibility = View.GONE
        binding.cardSearch.visibility = View.GONE // Sembunyikan search saat lihat list tausiah 🔍

        if (Account.isGuru()) {
            supportActionBar?.title = "Tausiah: $selectedSiswaNama"
        }

        lifecycleScope.launch {
            try {
                // Mengambil data tausiah spesifik milik siswa yang dipilih
                val data = controller.getTausiahSiswa(idUser)
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                
                if (!data.isNullOrEmpty()) {
                    val adapter = TausiahAdapter(data) { tausiah ->
                        val intent = Intent(this@TausiahActivity, AddTausiahActivity::class.java)
                        intent.putExtra("TAUSIAH_ID", tausiah.id)
                        intent.putExtra("TAUSIAH_JUDUL", tausiah.judulTausiah)
                        intent.putExtra("TAUSIAH_USTADZ", tausiah.namaPenceramah)
                        intent.putExtra("TAUSIAH_RINGKASAN", tausiah.ringkasan)
                        intent.putExtra("TAUSIAH_SUBMITTED", tausiah.isSubmitted)
                        intent.putExtra("TAUSIAH_TANGGAL", tausiah.tanggal)
                        tausiahLauncher.launch(intent)
                    }
                    binding.rvTausiah.layoutManager = LinearLayoutManager(this@TausiahActivity)
                    binding.rvTausiah.adapter = adapter
                    binding.rvTausiah.visibility = View.VISIBLE
                } else {
                    showEmptyState("Siswa ini belum memiliki catatan tausiah.")
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@TausiahActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showStudentList() {
        isViewingTausiahList = false
        binding.cardSearch.visibility = View.VISIBLE
        supportActionBar?.title = "Pilih Siswa"
        displayStudentList(allStudents)
    }

    private fun showEmptyState(msg: String) {
        binding.rvTausiah.visibility = View.GONE
        binding.layoutEmptyState.visibility = View.VISIBLE
        binding.tvEmptyDesc.text = msg
    }
}
