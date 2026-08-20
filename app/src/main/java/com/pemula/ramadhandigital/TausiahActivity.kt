package com.pemula.ramadhandigital

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.TausiahAdapter
import com.pemula.ramadhandigital.controller.TausiahController
import com.pemula.ramadhandigital.databinding.ActivityTausiahBinding
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.Tausiah
import kotlinx.coroutines.launch

class TausiahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTausiahBinding
    private val controller = TausiahController()

    private val tausiahLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadTausiah() 
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTausiahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        
        // MONYET FIX: Guru gak boleh nambah tausiah sendiri! 🍌🚫
        if (Account.isGuru()) {
            binding.fabAdd.visibility = View.GONE
            supportActionBar?.title = "Monitoring Tausiah"
        } else {
            binding.fabAdd.visibility = View.VISIBLE
            binding.fabAdd.setOnClickListener {
                val intent = Intent(this, AddTausiahActivity::class.java)
                intent.putExtra("TAUSIAH_ID", 0)
                tausiahLauncher.launch(intent)
            }
        }

        loadTausiah()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadTausiah() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val data = controller.getAllTausiah()
                binding.progressBar.visibility = View.GONE
                
                if (data != null) {
                    val adapter = TausiahAdapter(data) { tausiah ->
                        // KLIK KARTU: Buka halaman editor (AddTausiahActivity) 🚀
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
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@TausiahActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
