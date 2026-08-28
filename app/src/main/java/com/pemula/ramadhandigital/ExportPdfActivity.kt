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
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.gson.Gson
import com.pemula.ramadhandigital.adapter.TrackingSiswaAdapter
import com.pemula.ramadhandigital.controller.AbsensiController
import com.pemula.ramadhandigital.controller.IbadahHarianController
import com.pemula.ramadhandigital.databinding.ActivityExportPdfBinding
import com.pemula.ramadhandigital.model.*
import kotlinx.coroutines.*
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class ExportPdfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExportPdfBinding
    private val ibadahController = IbadahHarianController()
    private val absensiController = AbsensiController()
    
    private var currentDataList: List<IbadahHarian>? = null
    private var absensiDataList: List<AbsensiItem>? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { startPdfExport() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExportPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadData()

        binding.btnExport.setOnClickListener {
            checkPermissionAndExport()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Statistik & Rekapitulasi"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val currentDate = sdf.format(Date())
                val idKelasInt = Account.IdKelas

                val ibadahJob = async { ibadahController.getMonitoringKelas(idKelasInt, currentDate) }
                val absensiJob = async { absensiController.getAbsensi(idKelasInt, currentDate) }

                currentDataList = ibadahJob.await()
                absensiDataList = absensiJob.await()

                binding.progressBar.visibility = View.GONE
                updateUI()
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateUI() {
        val list = currentDataList ?: return
        val absensi = absensiDataList ?: emptyList()

        binding.tvTotalSiswa.text = list.size.toString()
        val totalHadir = absensi.count { it.idStatusAbsensi == 1 }
        binding.tvTotalHadir.text = totalHadir.toString()

        setupAbsensiChart(list.size, absensi)
        setupSholatChart(list)

        binding.rvSiswaSummary.layoutManager = LinearLayoutManager(this)
        binding.rvSiswaSummary.adapter = TrackingSiswaAdapter(list) { item ->
            val intent = Intent(this, DetailStatistikSiswaActivity::class.java)
            intent.putExtra("ITEM_DATA", Gson().toJson(item))
            startActivity(intent)
        }
    }

    private fun setupAbsensiChart(totalSiswa: Int, list: List<AbsensiItem>) {
        val hadir = list.count { it.idStatusAbsensi == 1 }.toFloat()
        val sakit = list.count { it.idStatusAbsensi == 2 }.toFloat()
        val izin = list.count { it.idStatusAbsensi == 3 }.toFloat()
        val alpha = list.count { it.idStatusAbsensi == 4 }.toFloat()
        val belum = (totalSiswa - list.size).coerceAtLeast(0).toFloat()

        val entries = mutableListOf<PieEntry>()
        if (hadir > 0) entries.add(PieEntry(hadir, "Hadir"))
        if (sakit > 0) entries.add(PieEntry(sakit, "Sakit"))
        if (izin > 0) entries.add(PieEntry(izin, "Izin"))
        if (alpha > 0) entries.add(PieEntry(alpha, "Alpha"))
        if (belum > 0) entries.add(PieEntry(belum, "Belum Absen"))

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf("#059669".toColorInt(), "#EAB308".toColorInt(), "#3B82F6".toColorInt(), "#DC2626".toColorInt(), "#94A3B8".toColorInt())
            valueTextColor = Color.WHITE
            valueTextSize = 10f
        }

        val pieChart = binding.pieChartAbsensi
        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(pieChart))

        pieChart.apply {
            data = pieData
            setUsePercentValues(true)
            description.isEnabled = false
            centerText = "Kehadiran\nKelas"
            animateY(800)
            invalidate()
        }
    }

    private fun setupSholatChart(list: List<IbadahHarian>) {
        var jamaah = 0f
        var munfarid = 0f
        var tidak = 0f
        var belum = 0f

        list.forEach { item ->
            val details = item.detailSholatWajibs ?: emptyList()
            jamaah += details.count { it.idStatusSholatWajib == 1 }
            munfarid += details.count { it.idStatusSholatWajib == 2 }
            tidak += details.count { it.idStatusSholatWajib == 3 }
            belum += (5 - details.size)
        }

        val entries = mutableListOf<PieEntry>()
        if (jamaah > 0) entries.add(PieEntry(jamaah, "Berjamaah"))
        if (munfarid > 0) entries.add(PieEntry(munfarid, "Munfarid"))
        if (tidak > 0) entries.add(PieEntry(tidak, "Tidak Sholat"))
        if (belum > 0) entries.add(PieEntry(belum, "Belum Diisi"))

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf("#15803D".toColorInt(), "#EAB308".toColorInt(), "#DC2626".toColorInt(), "#E2E8F0".toColorInt())
            valueTextColor = Color.DKGRAY
            valueTextSize = 10f
        }

        val pieChart = binding.pieChartSholat
        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(pieChart))

        pieChart.apply {
            data = pieData
            setUsePercentValues(true)
            description.isEnabled = false
            centerText = "Ibadah\nWajib"
            animateY(1000)
            invalidate()
        }
    }

    private fun checkPermissionAndExport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else startPdfExport()
        } else startPdfExport()
    }

    private fun startPdfExport() = exportToPdf(Account.IdKelas)

    private fun exportToPdf(idKelas: Int) {
        val list = currentDataList ?: return
        val absensi = absensiDataList ?: emptyList()
        lifecycleScope.launch {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val titlePaint = Paint().apply { isAntiAlias = true; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); color = "#004D40".toColorInt(); textSize = 18f }
            canvas.drawText("REKAPITULASI KELAS RAMADHAN", 150f, 60f, titlePaint)
            
            var yPos = 120f
            val paint = Paint().apply { isAntiAlias = true; textSize = 11f; color = Color.BLACK }
            list.forEach { item ->
                val status = absensi.find { it.idUser == item.idUser }?.statusAbsensi ?: "Belum Absen"
                canvas.drawText("${item.namaUser} : $status", 50f, yPos, paint)
                yPos += 20f
            }
            pdfDocument.finishPage(page)
            savePdfFile(pdfDocument, "REKAP_KELAS")
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
                withContext(Dispatchers.Main) { Toast.makeText(this@ExportPdfActivity, "PDF Berhasil diunduh", Toast.LENGTH_SHORT).show() }
            } finally {
                pdfDocument.close(); outputStream?.close()
            }
        }
    }
}
