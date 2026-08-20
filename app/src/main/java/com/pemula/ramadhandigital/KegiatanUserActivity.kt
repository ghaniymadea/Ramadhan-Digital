package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.KegiatanUserAdapter
import com.pemula.ramadhandigital.controller.KegiatanUserController
import com.pemula.ramadhandigital.databinding.ActivityKegiatanBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.KegiatanUser
import kotlinx.coroutines.launch

class KegiatanUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKegiatanBinding
    private val controller = KegiatanUserController()

    // Launcher untuk refresh data saat kembali dari halaman tulis 🍌🐒
    private val kegiatanLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadData() // Refresh list agar status update
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKegiatanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Kegiatan Pesantren"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // 1. Ambil data master kegiatan (Disediakan Guru) 🍌
                val masterData = controller.getAllKegiatan()
                // 2. Ambil catatan yang sudah diisi oleh Siswa 🐒
                val userRecords = controller.getKegiatanUser(Account.Id)
                
                binding.progressBar.visibility = View.GONE

                if (masterData != null) {
                    val listFinal = masterData.map { master ->
                        // Cek apakah siswa sudah mengisi kegiatan ini 🧐
                        val record = userRecords?.find { it.idKegiatan == master.id }
                        KegiatanUser(
                            id = record?.id ?: 0,
                            idUser = Account.Id,
                            idKegiatan = master.id,
                            note = record?.note,
                            user = null,
                            kegiatan = master
                        )
                    }

                    val adapter = KegiatanUserAdapter(listFinal) { item ->
                        // PINDAH KE HALAMAN EDITOR LUAS (AddKegiatanActivity) 🚀🔥
                        val intent = Intent(this@KegiatanUserActivity, AddKegiatanActivity::class.java)
                        intent.putExtra("ID_KEGIATAN", item.idKegiatan)
                        intent.putExtra("JUDUL", item.kegiatan?.judul)
                        intent.putExtra("USTADZ", item.kegiatan?.pemateri)
                        intent.putExtra("NOTE", item.note)
                        intent.putExtra("IS_SUBMITTED", !item.note.isNullOrEmpty())
                        kegiatanLauncher.launch(intent)
                    }
                    binding.rvKegiatan.layoutManager = LinearLayoutManager(this@KegiatanUserActivity)
                    binding.rvKegiatan.adapter = adapter
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@KegiatanUserActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
