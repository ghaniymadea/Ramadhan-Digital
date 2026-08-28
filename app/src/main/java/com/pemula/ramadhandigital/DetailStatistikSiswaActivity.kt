package com.pemula.ramadhandigital

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.gson.Gson
import com.pemula.ramadhandigital.controller.AbsensiController
import com.pemula.ramadhandigital.controller.IbadahHarianController
import com.pemula.ramadhandigital.controller.IbadahSunnahController
import com.pemula.ramadhandigital.databinding.ActivityDetailStatistikSiswaBinding
import com.pemula.ramadhandigital.model.*
import kotlinx.coroutines.*
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class DetailStatistikSiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStatistikSiswaBinding
    private val ibadahController = IbadahHarianController()
    private val sunnahController = IbadahSunnahController()
    private val absensiController = AbsensiController()
    
    private var itemData: IbadahHarian? = null
    private var currentIbadahFull: IbadahHarian? = null
    private var sunnahDataList: List<IbadahSunnah>? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startExport() else startExport()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStatistikSiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jsonItem = intent.getStringExtra("ITEM_DATA")
        itemData = try { Gson().fromJson(jsonItem, IbadahHarian::class.java) } catch (e: Exception) { null }

        setupToolbar()
        loadAllData()

        binding.btnExportIndividual.setOnClickListener {
            checkPermissionAndExport()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Statistik Ibadah Siswa"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadAllData() {
        val item = itemData ?: return
        binding.loadingProgress.visibility = View.VISIBLE
        binding.containerRincianSholat.removeAllViews()

        binding.tvNamaSiswaHeader.text = item.namaUser ?: "Tanpa Nama"
        binding.tvDetailInitial.text = if (!item.namaUser.isNullOrEmpty()) item.namaUser.take(1).uppercase() else "?"

        lifecycleScope.launch {
            try {
                val date = if (item.tanggal?.contains("T") == true) item.tanggal.split("T")[0] 
                          else item.tanggal ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                
                binding.tvTanggalDetail.text = "Laporan Tanggal: $date"

                val harianJob = async { ibadahController.getRekapSiswaSingleDate(item.idUser, date) }
                val sunnahJob = async { sunnahController.getSunnahSiswa(item.idUser, date) }
                val absensiJob = async { absensiController.getAbsensi(Account.IdKelas, date) }

                val harianResult = harianJob.await()
                sunnahDataList = sunnahJob.await()
                val absensiList = absensiJob.await()
                val studentAbsensi = absensiList?.find { it.idUser == item.idUser }

                withContext(Dispatchers.Main) {
                    currentIbadahFull = harianResult ?: item
                    
                    // Update Charts
                    setupAbsensiIndividuChart(studentAbsensi)
                    displayWajibData(currentIbadahFull!!)
                    processAndDisplaySunnah(sunnahDataList)
                    
                    // Update Badge
                    updateAbsensiBadge(studentAbsensi)
                    
                    binding.loadingProgress.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.e("DetailStatistik", "Error load: ${e.localizedMessage}")
                binding.loadingProgress.visibility = View.GONE
            }
        }
    }

    private fun setupAbsensiIndividuChart(item: AbsensiItem?) {
        val entries = mutableListOf<PieEntry>()
        if (item == null || item.idStatusAbsensi == 0) {
            entries.add(PieEntry(1f, "Belum Diabsen"))
        } else {
            entries.add(PieEntry(1f, item.statusAbsensi ?: "Sudah"))
        }

        val color = when (item?.idStatusAbsensi) {
            1 -> "#059669" // Hadir
            2, 3 -> "#EAB308" // Sakit/Izin
            4 -> "#DC2626" // Alpha
            else -> "#94A3B8" // Belum
        }.toColorInt()

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(color)
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        binding.pieChartAbsensiIndividu.apply {
            data = PieData(dataSet)
            setUsePercentValues(true)
            description.isEnabled = false
            centerText = "Kehadiran"
            animateY(800)
            invalidate()
        }
    }

    private fun displayWajibData(item: IbadahHarian) {
        val details = item.detailSholatWajibs ?: emptyList()
        
        val jamaah = details.count { it.idStatusSholatWajib == 1 }.toFloat()
        val munfarid = details.count { it.idStatusSholatWajib == 2 }.toFloat()
        val tidak = details.count { it.idStatusSholatWajib == 3 }.toFloat()
        val belum = (5 - details.size).coerceAtLeast(0).toFloat()

        val entries = mutableListOf<PieEntry>()
        if (jamaah > 0) entries.add(PieEntry(jamaah, "Berjamaah"))
        if (munfarid > 0) entries.add(PieEntry(munfarid, "Munfarid"))
        if (tidak > 0) entries.add(PieEntry(tidak, "Tidak"))
        if (belum > 0) entries.add(PieEntry(belum, "Belum Isi"))

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf("#059669".toColorInt(), "#EAB308".toColorInt(), "#DC2626".toColorInt(), "#E2E8F0".toColorInt())
            valueTextColor = Color.WHITE
            valueTextSize = 11f
        }

        val pieChart = binding.pieChartWajibIndividu
        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(pieChart))

        pieChart.setData(pieData)
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.centerText = "Wajib"
        pieChart.animateY(800)
        pieChart.invalidate()

        val quranInfo = if (item.membacaAlquran) "Al-Qur'an: Sudah (${item.targetBacaan ?: "-"})" else "Al-Qur'an: Belum"
        updateRincianList("IBADAH WAJIB", listOf(quranInfo) + details.map { "${it.kategori}: ${it.status ?: "Diisi"}" })
    }

    private fun processAndDisplaySunnah(list: List<IbadahSunnah>?) {
        val statusMap = mutableMapOf(1 to false, 2 to false, 3 to false, 4 to false, 5 to false)
        val names = mapOf(1 to "Tarawih", 2 to "Witir", 3 to "Dhuha", 4 to "Tahajud", 5 to "Sedekah")

        list?.forEach { item ->
            item.detailIbadahSunnahs?.forEach { if (it.isDone) statusMap[it.idKategoriIbadahSunnah] = true }
            if (item.flatIsDone) statusMap[item.flatIdKategori] = true
        }

        val doneCount = statusMap.values.count { it }.toFloat()
        val notDoneCount = (5 - doneCount).coerceAtLeast(0f)

        val entries = mutableListOf<PieEntry>()
        if (doneCount > 0) entries.add(PieEntry(doneCount, "Tuntas"))
        if (notDoneCount > 0) entries.add(PieEntry(notDoneCount, "Belum"))

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf("#15803D".toColorInt(), "#F1F5F9".toColorInt())
            valueTextColor = if (doneCount > 0) Color.WHITE else Color.TRANSPARENT
        }

        val pieChart = binding.pieChartSunnahIndividu
        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(pieChart))
        pieChart.setData(pieData)
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.centerText = "Sunnah"
        pieChart.animateY(1000)
        pieChart.invalidate()

        val rincianTexts = names.map { (id, name) -> "$name: ${if (statusMap[id] == true) "Selesai ✅" else "Belum"}" }
        updateRincianList("IBADAH SUNNAH", rincianTexts)
    }

    private fun updateAbsensiBadge(item: AbsensiItem?) {
        if (item == null) {
            binding.tvStatusAbsensiDetail.text = "BELUM DIABSEN GURU"
            binding.tvStatusAbsensiDetail.setTextColor("#64748B".toColorInt())
            return
        }
        binding.tvStatusAbsensiDetail.text = (item.statusAbsensi ?: "BELUM DIABSEN").uppercase()
        when (item.idStatusAbsensi) {
            1 -> { binding.tvStatusAbsensiDetail.setTextColor("#059669".toColorInt()) }
            2, 3 -> { binding.tvStatusAbsensiDetail.setTextColor("#D97706".toColorInt()) }
            else -> { binding.tvStatusAbsensiDetail.setTextColor("#DC2626".toColorInt()) }
        }
    }

    private fun updateRincianList(header: String, items: List<String>) {
        val headerView = TextView(this).apply {
            text = header; setPadding(0, 48, 0, 16); setTypeface(null, Typeface.BOLD); setTextColor("#004D40".toColorInt()); setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }
        binding.containerRincianSholat.addView(headerView)
        items.forEach { textItem ->
            val itemView = TextView(this).apply {
                text = "• $textItem"; setPadding(16, 4, 0, 4); setTextColor("#1E293B".toColorInt()); setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            }
            binding.containerRincianSholat.addView(itemView)
        }
    }

    private fun checkPermissionAndExport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else startExport()
        } else startExport()
    }

    private fun startExport() {
        val ibadah = currentIbadahFull ?: return
        lifecycleScope.launch {
            delay(1000)
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint().apply { isAntiAlias = true; color = Color.BLACK; textSize = 14f }
            canvas.drawText("REKAP IBADAH PRIBADI: ${ibadah.namaUser}", 50f, 50f, paint)
            pdfDocument.finishPage(page)
            savePdfFile(pdfDocument, "REKAP_${ibadah.namaUser?.replace(" ", "_")}")
        }
    }

    private fun savePdfFile(pdfDocument: PdfDocument, prefix: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val fileName = "${prefix}_${System.currentTimeMillis()}.pdf"
            var outputStream: OutputStream? = null
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply { put(MediaStore.MediaColumns.DISPLAY_NAME, fileName); put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf"); put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS) }
                    val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    if (uri != null) outputStream = contentResolver.openOutputStream(uri)
                } else {
                    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    outputStream = FileOutputStream(java.io.File(dir, fileName))
                }
                outputStream?.let { pdfDocument.writeTo(it) }
                withContext(Dispatchers.Main) { Toast.makeText(this@DetailStatistikSiswaActivity, "PDF Berhasil disimpan", Toast.LENGTH_SHORT).show() }
            } finally {
                pdfDocument.close(); outputStream?.close()
            }
        }
    }
}
