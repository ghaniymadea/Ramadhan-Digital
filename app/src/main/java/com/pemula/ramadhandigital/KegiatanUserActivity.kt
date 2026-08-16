package com.pemula.ramadhandigital

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pemula.ramadhandigital.adapter.KegiatanUserAdapter
import com.pemula.ramadhandigital.controller.*
import com.pemula.ramadhandigital.databinding.ActivityKegiatanBinding
import com.pemula.ramadhandigital.model.*
import kotlinx.coroutines.launch

class KegiatanUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKegiatanBinding
    
    // Semua Controller Bos di sini! 🍌🐒
    private val kegiatanController = KegiatanUserController()
    private val ibadahController = IbadahHarianController()
    private val sunnahController = IbadahSunnahController()
    private val tausiahController = TausiahController()
    private val setoranController = SetoranHafalanController()
    private val surahController = SurahController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKegiatanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kategori = intent.getStringExtra("KATEGORI") ?: "Kegiatan Ramadhan"
        setupToolbar(kategori)

        // Munculkan tombol tambah (+) khusus untuk kategori yang butuh input manual 🐒
        if (kategori.contains("IBADAH") || kategori.contains("APRSIASI") || 
            kategori == "CATATAN TAUSIAH" || kategori == "SETORAN HAFALAN") {
            binding.fabAdd.visibility = View.VISIBLE
            binding.fabAdd.setOnClickListener {
                when (kategori) {
                    "CATATAN APRESIASI IBADAH HARIAN" -> showIbadahDialog()
                    "CATATAN APRSIASI IBADAH SUNNAH RAMADHAN" -> showSunnahDialog()
                    "CATATAN TAUSIAH" -> showTausiahDialog()
                    "SETORAN HAFALAN" -> showSetoranDialog() // Dialog Baru! 🍌📖
                }
            }
        }

        loadKegiatan(kategori)
    }

    private fun setupToolbar(title: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadKegiatan(kategori: String) {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                Log.d("KegiatanUserActivity", "Memulai request untuk kategori: $kategori")

                val listKegiatanUser = when (kategori) {
                    "CATATAN KEGIATAN PESANTREN RAMADHAN" -> {
                        val masterData = kegiatanController.getAllKegiatan()
                        masterData?.map {
                            KegiatanUser(id = 0, idUser = Account.Id, idKegiatan = it.id, note = null, user = null, kegiatan = it)
                        }
                    }
                    "CATATAN APRESIASI IBADAH HARIAN" -> {
                        val ibadahData = ibadahController.getIbadahHarianHariIni()
                        ibadahData?.map {
                            KegiatanUser(id = it.id, idUser = it.idUser, idKegiatan = 0, note = "", user = null,
                                kegiatan = Kegiatan(id = 0, judul = "Ibadah Harian Hari Ini", pemateri = "Target: ${it.targetBacaan ?: "-"}", tanggal = it.tanggal, kegiatanUsers = null, jam = if (it.membacaAlquran) "Al-Quran: Ya" else "Al-Quran: Tidak"))
                        }
                    }
                    "CATATAN APRSIASI IBADAH SUNNAH RAMADHAN" -> {
                        val sunnahData = sunnahController.getMyIbadahSunnahHariIni()
                        sunnahData?.map {
                            KegiatanUser(id = it.id, idUser = it.idUser, idKegiatan = 0, note = "", user = null,
                                kegiatan = Kegiatan(id = 0, judul = it.kategori?.nama ?: "Ibadah Sunnah", pemateri = "Mandiri", tanggal = it.tanggal, kegiatanUsers = null, jam = "Dikerjakan"))
                        }
                    }
                    "CATATAN TAUSIAH" -> {
                        val tausiahData = tausiahController.getAllTausiah()
                        tausiahData?.map {
                            KegiatanUser(id = it.id, idUser = it.idUser, idKegiatan = 0, note = it.ringkasan ?: "", user = null,
                                kegiatan = Kegiatan(id = 0, judul = it.judulTausiah ?: "Tausiah", pemateri = it.namaPenceramah ?: "-", tanggal = it.tanggal, kegiatanUsers = null, jam = "Tersimpan"))
                        }
                    }
                    "SETORAN HAFALAN" -> {
                        // AMBIL DATA SETORAN DARI BACKEND C#! 🍌🚀
                        val setoranData = setoranController.getSetoranByUser(Account.Id)
                        setoranData?.map {
                            KegiatanUser(id = it.id, idUser = it.idUser, idKegiatan = 0, note = it.note ?: "", user = null,
                                kegiatan = Kegiatan(id = 0, judul = "Hafalan: ${it.surah?.surahName ?: "Surah"}", pemateri = "Status: ${it.status?.nama ?: "Menunggu"}", tanggal = it.tanggalSetoran, kegiatanUsers = null, jam = "Lihat Catatan"))
                        }
                    }
                    else -> {
                        kegiatanController.getKegiatanUser(Account.Id)
                    }
                }

                binding.progressBar.visibility = View.GONE

                if (listKegiatanUser != null && listKegiatanUser.isNotEmpty()) {
                    val adapter = KegiatanUserAdapter(listKegiatanUser) { item ->
                        when (kategori) {
                            "CATATAN APRESIASI IBADAH HARIAN" -> showIbadahDialog()
                            "CATATAN APRSIASI IBADAH SUNNAH RAMADHAN" -> showSunnahDialog()
                            "CATATAN TAUSIAH" -> showTausiahDialog()
                            "SETORAN HAFALAN" -> Toast.makeText(this@KegiatanUserActivity, "Note: ${item.note}", Toast.LENGTH_LONG).show()
                            else -> showNoteDialog(item.idKegiatan, item.kegiatan?.judul ?: "Kegiatan")
                        }
                    }
                    binding.rvKegiatan.layoutManager = LinearLayoutManager(this@KegiatanUserActivity)
                    binding.rvKegiatan.adapter = adapter
                } else {
                    if (kategori != "CATATAN KEGIATAN PESANTREN RAMADHAN" && !kategori.contains("IBADAH") && 
                        kategori != "CATATAN TAUSIAH" && kategori != "SETORAN HAFALAN") {
                        Toast.makeText(this@KegiatanUserActivity, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Log.e("KegiatanUserActivity", "Error Load: ${e.message}")
                Toast.makeText(this@KegiatanUserActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSetoranDialog() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val listSurah = surahController.getJuzAmma() // Ambil list surah biar Bos tinggal pilih 🍌
            binding.progressBar.visibility = View.GONE
            
            if (listSurah == null) {
                Toast.makeText(this@KegiatanUserActivity, "Gagal memuat daftar surah", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val builder = AlertDialog.Builder(this@KegiatanUserActivity)
            builder.setTitle("Input Setoran Hafalan")
            val layout = LinearLayout(this@KegiatanUserActivity).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(50, 40, 50, 10)
            }

            // Spinner Pilih Surah 📖
            val tvSurah = TextView(this@KegiatanUserActivity).apply { text = "Pilih Surah:" }
            val spinnerSurah = Spinner(this@KegiatanUserActivity)
            val surahNames = listSurah.map { it.surahName ?: "Surah" }
            spinnerSurah.adapter = ArrayAdapter(this@KegiatanUserActivity, android.R.layout.simple_spinner_dropdown_item, surahNames)

            // Spinner Pilih Status 🐒
            val tvStatus = TextView(this@KegiatanUserActivity).apply { text = "\nStatus Hafalan:" }
            val spinnerStatus = Spinner(this@KegiatanUserActivity)
            val statusNames = arrayOf("Lancar", "Kurang Lancar", "Terbata-bata", "Baru Menghafal")
            val statusIds = intArrayOf(1, 2, 3, 4) 
            spinnerStatus.adapter = ArrayAdapter(this@KegiatanUserActivity, android.R.layout.simple_spinner_dropdown_item, statusNames)

            val etNote = EditText(this@KegiatanUserActivity).apply { hint = "Catatan Tambahan (Misal: Ayat 1-10)" }

            layout.addView(tvSurah); layout.addView(spinnerSurah)
            layout.addView(tvStatus); layout.addView(spinnerStatus)
            layout.addView(etNote)
            builder.setView(layout)

            builder.setPositiveButton("Setorkan") { _, _ ->
                val surahId = listSurah[spinnerSurah.selectedItemPosition].id
                val statusId = statusIds[spinnerStatus.selectedItemPosition]
                simpanSetoranKeAPI(surahId, statusId, etNote.text.toString())
            }
            builder.setNegativeButton("Batal", null).show()
        }
    }

    private fun simpanSetoranKeAPI(idSurah: Int, idStatus: Int, note: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val sukses = setoranController.createSetoran(idSurah, idStatus, note)
            binding.progressBar.visibility = View.GONE
            if (sukses) {
                Toast.makeText(this@KegiatanUserActivity, "Setoran berhasil disimpan!", Toast.LENGTH_SHORT).show()
                loadKegiatan("SETORAN HAFALAN")
            } else {
                Toast.makeText(this@KegiatanUserActivity, "Gagal simpan setoran", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showTausiahDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Input Catatan Tausiah")
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(50, 40, 50, 10) }
        val etJudul = EditText(this).apply { hint = "Judul Tausiah" }
        val etPenceramah = EditText(this).apply { hint = "Nama Penceramah" }
        val etRingkasan = EditText(this).apply { hint = "Ringkasan"; minLines = 3 }
        layout.addView(etJudul); layout.addView(etPenceramah); layout.addView(etRingkasan)
        builder.setView(layout)
        builder.setPositiveButton("Simpan") { _, _ ->
            if (etJudul.text.isNotEmpty()) simpanTausiahKeAPI(etJudul.text.toString(), etPenceramah.text.toString(), etRingkasan.text.toString())
            else Toast.makeText(this, "Judul wajib diisi!", Toast.LENGTH_SHORT).show()
        }.setNegativeButton("Batal", null).show()
    }

    private fun simpanTausiahKeAPI(judul: String, penceramah: String, ringkasan: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            if (tausiahController.createTausiah(judul, penceramah, ringkasan)) {
                Toast.makeText(this@KegiatanUserActivity, "Tausiah tersimpan!", Toast.LENGTH_SHORT).show()
                loadKegiatan("CATATAN TAUSIAH")
            } else binding.progressBar.visibility = View.GONE
        }
    }

    private fun showIbadahDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Input Ibadah Harian")
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(50, 40, 50, 10) }
        val cbQuran = CheckBox(this).apply { text = "Membaca Al-Quran" }
        val etTarget = EditText(this).apply { hint = "Target Bacaan (Contoh: Juz 30)" }
        layout.addView(cbQuran); layout.addView(etTarget)
        builder.setView(layout)
        builder.setPositiveButton("Simpan") { _, _ -> simpanIbadahKeAPI(etTarget.text.toString(), cbQuran.isChecked) }
        builder.setNegativeButton("Batal", null).show()
    }

    private fun simpanIbadahKeAPI(target: String, membacaAlquran: Boolean) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            if (ibadahController.registerIbadahHarian(target, membacaAlquran)) {
                Toast.makeText(this@KegiatanUserActivity, "Ibadah tersimpan!", Toast.LENGTH_SHORT).show()
                loadKegiatan("CATATAN APRESIASI IBADAH HARIAN")
            } else binding.progressBar.visibility = View.GONE
        }
    }

    private fun showSunnahDialog() {
        val names = arrayOf("Sholat Tahajud", "Sholat Dhuha", "Sholat Witir", "Sholat Rawatib", "Sedekah")
        val ids = intArrayOf(1, 2, 3, 4, 5); val checked = BooleanArray(names.size)
        AlertDialog.Builder(this).setTitle("Pilih Ibadah Sunnah")
            .setMultiChoiceItems(names, checked) { _, which, isChecked -> checked[which] = isChecked }
            .setPositiveButton("Simpan") { _, _ ->
                val selected = mutableListOf<Int>()
                for (i in checked.indices) if (checked[i]) selected.add(ids[i])
                if (selected.isNotEmpty()) simpanSunnahKeAPI(selected)
            }.setNegativeButton("Batal", null).show()
    }

    private fun simpanSunnahKeAPI(selectedIds: List<Int>) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            if (sunnahController.saveIbadahSunnah(selectedIds)) {
                Toast.makeText(this@KegiatanUserActivity, "Sunnah tersimpan!", Toast.LENGTH_SHORT).show()
                loadKegiatan("CATATAN APRSIASI IBADAH SUNNAH RAMADHAN")
            } else binding.progressBar.visibility = View.GONE
        }
    }

    private fun showNoteDialog(idKegiatan: Int, judul: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tambah Catatan").setMessage("Tulis kegiatanmu di '$judul'")
        val input = EditText(this).apply { hint = "Contoh: Sudah ikut tadarus..." }
        val container = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(50, 0, 50, 0) }
        container.addView(input)
        builder.setView(container)
        builder.setPositiveButton("Kirim") { _, _ -> if (input.text.isNotEmpty()) simpanKeAPI(idKegiatan, input.text.toString()) }
        builder.setNegativeButton("Batal", null).show()
    }

    private fun simpanKeAPI(idKegiatan: Int, note: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            if (kegiatanController.registerKegiatan(Account.Id, idKegiatan, note)) {
                Toast.makeText(this@KegiatanUserActivity, "Berhasil simpan!", Toast.LENGTH_SHORT).show()
                loadKegiatan(intent.getStringExtra("KATEGORI") ?: "")
            } else binding.progressBar.visibility = View.GONE
        }
    }
}
