package com.pemula.ramadhandigital

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
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
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ExportPdfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExportPdfBinding
    private val ibadahController = IbadahHarianController()
    private val absensiController = AbsensiController()
    
    private var currentDataList: List<IbadahHarian>? = null
    private var absensiDataList: List<AbsensiItem>? = null
    private var activeReportDate: String = "" // Simpan tanggal data yang sedang ditampilkan 📅

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> startPdfExport() }

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
                activeReportDate = sdf.format(Date()) // Default hari ini
                val idKelasInt = Account.IdKelas

                val ibadahJob = async { ibadahController.getMonitoringKelas(idKelasInt, activeReportDate) }
                val absensiJob = async { absensiController.getAbsensi(idKelasInt, activeReportDate) }

                currentDataList = ibadahJob.await()
                absensiDataList = absensiJob.await()

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    updateUI()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Log.e("ExportPdf", "Error loading: ${e.message}")
            }
        }
    }

    private fun formatIndoDate(dateStr: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val output = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val date = input.parse(dateStr)
            output.format(date!!)
        } catch (e: Exception) { dateStr }
    }

    private fun updateUI() {
        val list = currentDataList ?: return
        val absensi = absensiDataList ?: emptyList()

        binding.tvTotalSiswa.text = list.size.toString()
        val totalHadir = absensi.count { it.idStatusAbsensi == 1 }
        binding.tvTotalHadir.text = totalHadir.toString()

        setupAbsensiChart(list.size, absensi)

        binding.rvSiswaSummary.layoutManager = LinearLayoutManager(this)
        binding.rvSiswaSummary.adapter = TrackingSiswaAdapter(list) { item ->
            // Pastikan data tanggal terbawa ke halaman detail agar statistik sinkron 🚀
            val intent = Intent(this, DetailStatistikSiswaActivity::class.java)
            val updatedItem = item.copy(tanggal = activeReportDate)
            intent.putExtra("ITEM_DATA", Gson().toJson(updatedItem))
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
        val colors = mutableListOf<Int>()

        if (hadir > 0) { entries.add(PieEntry(hadir, "Hadir")); colors.add("#059669".toColorInt()) }
        if (sakit > 0) { entries.add(PieEntry(sakit, "Sakit")); colors.add("#EAB308".toColorInt()) }
        if (izin > 0) { entries.add(PieEntry(izin, "Izin")); colors.add("#3B82F6".toColorInt()) }
        if (alpha > 0) { entries.add(PieEntry(alpha, "Alpha")); colors.add("#DC2626".toColorInt()) }
        if (belum > 0) { entries.add(PieEntry(belum, "Belum")); colors.add("#94A3B8".toColorInt()) }

        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            valueTextColor = Color.WHITE
            valueTextSize = 11f
        }

        val pieChart = binding.pieChartAbsensi
        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(pieChart))
        
        pieChart.apply {
            data = pieData
            setUsePercentValues(true)
            description.isEnabled = false
            centerText = "Status Kehadiran"
            animateY(800)
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

    private fun startPdfExport() {
        val list = currentDataList ?: return
        val absensi = absensiDataList ?: emptyList()
        lifecycleScope.launch {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            
            val titlePaint = Paint().apply { isAntiAlias = true; typeface = Typeface.DEFAULT_BOLD; textSize = 18f; color = Color.BLACK }
            val headerPaint = Paint().apply { isAntiAlias = true; typeface = Typeface.DEFAULT_BOLD; textSize = 11f; color = Color.WHITE }
            val headerBgPaint = Paint().apply { color = "#004D40".toColorInt() }
            val textPaint = Paint().apply { isAntiAlias = true; textSize = 10f; color = Color.BLACK }
            val linePaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 0.5f }
            
            var y = 60f
            canvas.drawText("LAPORAN ABSENSI SISWA RAMADHAN", 50f, y, titlePaint)
            y += 25f
            // GUNAKAN TANGGAL DATA, BUKAN TANGGAL HARI INI 📅
            canvas.drawText(String.format(Locale.US, "Rekapitulasi Tanggal: %s", formatIndoDate(activeReportDate)), 50f, y, textPaint)
            y += 40f
            
            // Header Tabel
            canvas.drawRect(50f, y - 15f, 545f, y + 5f, headerBgPaint)
            canvas.drawText("NO", 60f, y, headerPaint)
            canvas.drawText("NAMA SISWA", 90f, y, headerPaint)
            canvas.drawText("STATUS KEHADIRAN", 350f, y, headerPaint)
            y += 20f
            
            list.forEachIndexed { index, item ->
                val statusAbsen = absensi.find { it.idUser == item.idUser }?.statusAbsensi ?: "Belum Absen"
                
                canvas.drawText("${index + 1}", 60f, y, textPaint)
                canvas.drawText(item.namaUser ?: "-", 90f, y, textPaint)
                canvas.drawText(statusAbsen, 350f, y, textPaint)
                
                y += 5f
                canvas.drawLine(50f, y, 545f, y, linePaint)
                y += 15f
            }
            
            pdfDocument.finishPage(page)
            savePdfFile(pdfDocument, String.format(Locale.US, "LAPORAN_ABSENSI_%s", activeReportDate))
        }
    }

    private fun savePdfFile(pdfDocument: PdfDocument, prefix: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val fileName = String.format(Locale.US, "%s_%d.pdf", prefix, System.currentTimeMillis())
            var fileUri: Uri?
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply { 
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    fileUri = uri
                    uri?.let { contentResolver.openOutputStream(it)?.use { out -> pdfDocument.writeTo(out) } }
                } else {
                    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                    FileOutputStream(file).use { out -> pdfDocument.writeTo(out) }
                    fileUri = FileProvider.getUriForFile(this@ExportPdfActivity, String.format(Locale.US, "%s.provider", packageName), file)
                }
                
                withContext(Dispatchers.Main) { 
                    Toast.makeText(this@ExportPdfActivity, "PDF Berhasil disimpan", Toast.LENGTH_SHORT).show()
                    showNotification(fileName, fileUri)
                }
            } catch (e: Exception) { 
                Log.e("ExportPdf", "Save error: ${e.message}")
            } finally { pdfDocument.close() }
        }
    }

    private fun showNotification(fileName: String, fileUri: Uri?) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val chanId = "rekap_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(NotificationChannel(chanId, "Laporan PDF", NotificationManager.IMPORTANCE_HIGH))
        }
        val intent = Intent(Intent.ACTION_VIEW).apply { 
            setDataAndType(fileUri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val notif = NotificationCompat.Builder(this, chanId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Laporan Absensi Selesai")
            .setContentText(fileName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        nm.notify(1001, notif)
    }
}
