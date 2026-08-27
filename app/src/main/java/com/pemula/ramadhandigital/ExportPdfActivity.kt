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
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.pemula.ramadhandigital.adapter.TrackingSiswaAdapter
import com.pemula.ramadhandigital.controller.IbadahHarianController
import com.pemula.ramadhandigital.databinding.ActivityExportPdfBinding
import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.model.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class ExportPdfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExportPdfBinding
    private val ibadahController = IbadahHarianController()
    private var currentDataList: List<IbadahHarian>? = null

    private val channelId = "export_pdf_channel_v17"
    private val notificationId = 1717

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) startPdfExport()
        else {
            Toast.makeText(this, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
            startPdfExport()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExportPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        setupToolbar()
        loadData()

        binding.btnExport.setOnClickListener {
            checkPermissionAndExport()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Ekspor Laporan"
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
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

    private fun showNotification(title: String, message: String, isFinished: Boolean = false, fileUri: Uri? = null) {
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(if (isFinished) android.R.drawable.stat_sys_download_done else android.R.drawable.stat_sys_download)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(!isFinished)
            .setAutoCancel(isFinished)

        if (!isFinished) {
            builder.setProgress(0, 0, true)
        } else if (fileUri != null) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )
            builder.setContentIntent(pendingIntent)
        }

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(this@ExportPdfActivity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                notify(notificationId, builder.build())
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Rekapitulasi & Ekspor"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val currentDate = sdf.format(Date())
                val idKelasInt = Account.IdKelas

                // FIX: Menggunakan getMonitoringKelas (Bukan getAbsensi) 🚀
                val listData = ibadahController.getMonitoringKelas(idKelasInt, currentDate)
                binding.progressBar.visibility = View.GONE

                if (listData != null) {
                    currentDataList = listData
                    updateUI(listData)
                } else {
                    Toast.makeText(this@ExportPdfActivity, "Data monitoring tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateUI(list: List<IbadahHarian>) {
        binding.tvTotalSiswa.text = list.size.toString()
        val fillCount = list.count { it.sudahMengisi }
        binding.tvTotalHadir.text = fillCount.toString()

        setupPieChart(list)

        binding.rvSiswaSummary.layoutManager = LinearLayoutManager(this)
        binding.rvSiswaSummary.adapter = TrackingSiswaAdapter(list) { }
    }

    private fun setupPieChart(list: List<IbadahHarian>) {
        val entries = mutableListOf<PieEntry>()
        val total = list.size.toFloat()
        val tuntas = list.count { it.sudahMengisi }.toFloat()

        if (total > 0) {
            entries.add(PieEntry(tuntas, "Sudah Mengisi"))
            entries.add(PieEntry(total - tuntas, "Belum Mengisi"))
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf("#059669".toColorInt(), "#DC2626".toColorInt())
            valueTextSize = 13f
            valueTextColor = Color.WHITE
            sliceSpace = 3f
        }

        val pieChart = binding.pieChartExport
        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(pieChart))

        pieChart.apply {
            this.data = pieData
            setUsePercentValues(true)
            description.isEnabled = false
            legend.isEnabled = true
            setHoleColor(Color.WHITE)
            animateY(800)
            invalidate()
        }
    }

    private fun getHighResChartBitmap(chart: PieChart): Bitmap {
        val width = chart.width * 3
        val height = chart.height * 3
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.scale(3f, 3f)
        chart.draw(canvas)
        return bitmap
    }

    private fun exportToPdf(idKelas: Int) {
        val list = currentDataList ?: return
        showNotification("Ramadhan Digital", "Menyiapkan laporan PDF HD...", false)

        lifecycleScope.launch {
            delay(1000)
            val pdfDocument = PdfDocument()
            val paint = Paint().apply { isAntiAlias = true; isFilterBitmap = true; isDither = true }
            val titlePaint = Paint().apply { isAntiAlias = true; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); color = "#004D40".toColorInt(); textSize = 22f }

            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            canvas.drawText("LAPORAN MONITORING RAMADHAN DIGITAL", 80f, 65f, titlePaint)
            paint.textSize = 12f; paint.color = Color.DKGRAY
            val idLocale = Locale("id", "ID")
            val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", idLocale)
            canvas.drawText("Dicetak pada: ${sdf.format(Date())}", 50f, 95f, paint)
            canvas.drawText("Kelas ID: $idKelas", 50f, 115f, paint)
            paint.color = "#EEEEEE".toColorInt(); canvas.drawLine(50f, 130f, 545f, 130f, paint)

            paint.color = Color.BLACK; paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); paint.textSize = 16f
            canvas.drawText("Ringkasan Kehadiran Ibadah", 50f, 165f, paint)

            try {
                val chartBitmap = getHighResChartBitmap(binding.pieChartExport)
                val destRect = RectF(300f, 145f, 540f, 345f)
                canvas.drawBitmap(chartBitmap, null, destRect, paint)
            } catch (e: Exception) { }

            var yPos = 400f
            paint.color = "#004D40".toColorInt(); canvas.drawRect(50f, yPos - 25f, 545f, yPos + 10f, paint)
            paint.color = Color.WHITE; paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); paint.textSize = 12f
            canvas.drawText("NO", 65f, yPos, paint); canvas.drawText("NAMA LENGKAP SISWA", 110f, yPos, paint); canvas.drawText("STATUS", 410f, yPos, paint)

            yPos += 35f; paint.color = Color.BLACK; paint.typeface = Typeface.DEFAULT
            val statusPaint = Paint().apply { isAntiAlias = true; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textSize = 11f }

            list.forEachIndexed { index, item ->
                if (yPos < 800) {
                    canvas.drawText("${index + 1}", 65f, yPos, paint)
                    canvas.drawText((item.namaUser ?: "Siswa").uppercase(), 110f, yPos, paint)

                    val statusStr = if (item.sudahMengisi) "SUDAH MENGISI" else "BELUM MENGISI"
                    statusPaint.color = if (item.sudahMengisi) "#059669".toColorInt() else "#DC2626".toColorInt()
                    canvas.drawText(statusStr, 410f, yPos, statusPaint)

                    paint.color = "#F5F5F5".toColorInt(); canvas.drawLine(50f, yPos + 5f, 545f, yPos + 5f, paint)
                    paint.color = Color.BLACK; yPos += 28f
                }
            }
            pdfDocument.finishPage(page)
            savePdfFile(pdfDocument)
        }
    }

    private fun savePdfFile(pdfDocument: PdfDocument) {
        lifecycleScope.launch(Dispatchers.IO) {
            val fileName = "LAPORAN_HD_${System.currentTimeMillis()}.pdf"
            var outputStream: OutputStream? = null
            var savedUri: Uri? = null

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    savedUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    if (savedUri != null) outputStream = contentResolver.openOutputStream(savedUri)
                } else {
                    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    if (!dir.exists()) dir.mkdirs()
                    val file = File(dir, fileName)
                    outputStream = FileOutputStream(file)
                    savedUri = FileProvider.getUriForFile(this@ExportPdfActivity, "${packageName}.provider", file)
                }

                outputStream?.let {
                    pdfDocument.writeTo(it)
                    withContext(Dispatchers.Main) {
                        showNotification("Unduhan Selesai", "Laporan HD berhasil disimpan. Ketuk untuk membuka.", true, savedUri)
                        Toast.makeText(this@ExportPdfActivity, "PDF HD Berhasil diunduh!", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { showNotification("Gagal", "Gagal menyimpan file", true) }
            } finally {
                pdfDocument.close()
                try { outputStream?.close() } catch (e: Exception) {}
            }
        }
    }
}
