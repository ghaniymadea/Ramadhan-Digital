package com.pemula.ramadhandigital

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
        supportActionBar?.title = "Rekapitulasi Absensi"
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
            val output = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
            val date = input.parse(dateStr)
            output.format(date!!)
        } catch (e: Exception) { 
            Log.e("ExportPdf", "Date parsing error: ${e.message}")
            dateStr 
        }
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
        val izin = list.count { it.idStatusAbsensi == 2 }.toFloat()
        val sakit = list.count { it.idStatusAbsensi == 3 }.toFloat()
        val alpha = list.count { it.idStatusAbsensi == 4 }.toFloat()
        val belum = (totalSiswa - list.size).coerceAtLeast(0).toFloat()

        val entries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()

        if (hadir > 0) { entries.add(PieEntry(hadir, "Hadir")); colors.add("#059669".toColorInt()) }
        if (izin > 0) { entries.add(PieEntry(izin, "Izin")); colors.add("#EAB308".toColorInt()) }
        if (sakit > 0) { entries.add(PieEntry(sakit, "Sakit")); colors.add("#3B82F6".toColorInt()) }
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
            val pageWidth = 595
            val pageHeight = 842
            var pageNumber = 1
            
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas
            
            // Paints
            val titlePaint = Paint().apply { isAntiAlias = true; typeface = Typeface.DEFAULT_BOLD; textSize = 18f; color = "#004D40".toColorInt() }
            val subTitlePaint = Paint().apply { isAntiAlias = true; textSize = 10f; color = Color.DKGRAY }
            val headerPaint = Paint().apply { isAntiAlias = true; typeface = Typeface.DEFAULT_BOLD; textSize = 11f; color = Color.WHITE }
            val headerBgPaint = Paint().apply { color = "#00796B".toColorInt() }
            val textPaint = Paint().apply { isAntiAlias = true; textSize = 10f; color = Color.BLACK }
            val linePaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 0.5f }
            val statusPaint = Paint().apply { isAntiAlias = true; textSize = 10f; typeface = Typeface.DEFAULT_BOLD }

            var y: Float
            
            // Draw Header
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 40f, headerBgPaint)
            canvas.drawText("LAPORAN ABSENSI HARIAN", 50f, 75f, titlePaint)
            y = 95f
            canvas.drawText("Kelas: ${Account.Kelas ?: "-"}", 50f, y, subTitlePaint)
            y += 15f
            canvas.drawText("Tanggal: ${formatIndoDate(activeReportDate)}", 50f, y, subTitlePaint)
            y += 30f
            
            // Draw Summary Box
            val summaryBg = Paint().apply { color = "#F1F5F9".toColorInt() }
            canvas.drawRoundRect(50f, y, 545f, y + 40f, 8f, 8f, summaryBg)
            val summaryTextPaint = Paint().apply { isAntiAlias = true; textSize = 11f; typeface = Typeface.DEFAULT_BOLD }
            
            val hadirCount = absensi.count { it.idStatusAbsensi == 1 }
            val izinCount = absensi.count { it.idStatusAbsensi == 2 }
            val sakitCount = absensi.count { it.idStatusAbsensi == 3 }
            val alphaCount = absensi.count { it.idStatusAbsensi == 4 }
            
            canvas.drawText("Hadir: $hadirCount", 70f, y + 25f, summaryTextPaint)
            canvas.drawText("Izin: $izinCount", 180f, y + 25f, summaryTextPaint)
            canvas.drawText("Sakit: $sakitCount", 280f, y + 25f, summaryTextPaint)
            canvas.drawText("Alpha: $alphaCount", 380f, y + 25f, summaryTextPaint)
            
            y += 70f
            
            // Table Headers (Hanya Absensi)
            val colNo = 50f
            val colNama = 90f
            val colAbsen = 400f
            
            canvas.drawRect(50f, y - 18f, 545f, y + 8f, headerBgPaint)
            canvas.drawText("NO", colNo + 5, y, headerPaint)
            canvas.drawText("NAMA LENGKAP SISWA", colNama, y, headerPaint)
            canvas.drawText("STATUS KEHADIRAN", colAbsen, y, headerPaint)
            
            y += 25f
            
            list.forEachIndexed { index, item ->
                if (y > pageHeight - 50f) {
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    y = 50f
                    
                    canvas.drawRect(50f, y - 18f, 545f, y + 8f, headerBgPaint)
                    canvas.drawText("NO", colNo + 5, y, headerPaint)
                    canvas.drawText("NAMA LENGKAP SISWA", colNama, y, headerPaint)
                    canvas.drawText("STATUS KEHADIRAN", colAbsen, y, headerPaint)
                    y += 25f
                }

                val statusAbsen = absensi.find { it.idUser == item.idUser }?.statusAbsensi ?: "Belum Absen"
                
                canvas.drawText("${index + 1}", colNo + 5, y, textPaint)
                canvas.drawText(item.namaUser ?: "-", colNama, y, textPaint)
                
                statusPaint.color = when(statusAbsen) {
                    "Hadir" -> "#059669".toColorInt()
                    "Izin", "Sakit" -> "#EAB308".toColorInt()
                    "Alpha" -> "#DC2626".toColorInt()
                    else -> Color.GRAY
                }
                canvas.drawText(statusAbsen, colAbsen, y, statusPaint)
                
                y += 5f
                canvas.drawLine(50f, y, 545f, y, linePaint)
                y += 20f
            }
            
            canvas.drawText("Dicetak pada: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}", 50f, pageHeight - 30f, subTitlePaint)
            
            pdfDocument.finishPage(page)
            savePdfFile(pdfDocument, String.format(Locale.US, "REKAP_ABSENSI_%s", activeReportDate))
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
            .setContentTitle("Laporan PDF Selesai")
            .setContentText(fileName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        nm.notify(1001, notif)
    }
}
