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
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.gson.Gson
import com.pemula.ramadhandigital.controller.IbadahHarianController
import com.pemula.ramadhandigital.controller.IbadahSunnahController
import com.pemula.ramadhandigital.databinding.ActivityDetailStatistikSiswaBinding
import com.pemula.ramadhandigital.model.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class DetailStatistikSiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStatistikSiswaBinding
    private val ibadahController = IbadahHarianController()
    private val sunnahController = IbadahSunnahController()
    
    private var itemData: IbadahHarian? = null
    private var currentIbadahFull: IbadahHarian? = null
    private var sunnahDataList: List<IbadahSunnah>? = null
    private var activeDate: String = ""

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> startExport() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStatistikSiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jsonItem = intent.getStringExtra("ITEM_DATA")
        itemData = try { Gson().fromJson(jsonItem, IbadahHarian::class.java) } catch (e: Exception) { null }

        setupToolbar()
        setupSwipeRefresh()
        loadAllData(showProgress = true)

        binding.btnExportIndividual.setOnClickListener {
            checkPermissionAndExport()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Statistik Ibadah"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeColors("#004D40".toColorInt(), "#2E7D32".toColorInt())
        binding.swipeRefresh.setOnRefreshListener {
            loadAllData(showProgress = false)
        }
    }

    private fun loadAllData(showProgress: Boolean = true) {
        val item = itemData ?: return
        val studentId = if (item.idUser != 0) item.idUser else item.id
        
        if (showProgress) binding.loadingProgress.visibility = View.VISIBLE
        
        binding.tvNamaSiswaHeader.text = item.namaUser ?: "Tanpa Nama"
        binding.tvDetailInitial.text = if (!item.namaUser.isNullOrEmpty()) item.namaUser.take(1).uppercase() else "?"

        lifecycleScope.launch {
            try {
                activeDate = if (item.tanggal?.contains("T") == true) item.tanggal.split("T")[0] 
                          else item.tanggal ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                
                binding.tvTanggalDetail.text = String.format(Locale.US, "Laporan Tanggal: %s", formatNiceDate(activeDate))

                // Parallel fetch menggunakan ID Siswa yang valid 🚀
                val harianJob = async { ibadahController.getRekapSiswaSingleDate(studentId, activeDate) }
                val sunnahJob = async { sunnahController.getSunnahSiswa(studentId, activeDate) }

                val harianResult = harianJob.await()
                sunnahDataList = sunnahJob.await()

                withContext(Dispatchers.Main) {
                    binding.containerRincianSholat.removeAllViews()
                    currentIbadahFull = harianResult ?: item
                    
                    displayWajibData(currentIbadahFull!!)
                    processAndDisplaySunnah(sunnahDataList)
                    
                    binding.loadingProgress.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                }
            } catch (e: Exception) {
                Log.e("DetailStatistik", "Error: ${e.message}")
                binding.loadingProgress.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun formatNiceDate(dateStr: String): String {
        return try {
            val inputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputSdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val date = inputSdf.parse(dateStr)
            outputSdf.format(date!!)
        } catch (e: Exception) { dateStr }
    }

    private fun displayWajibData(item: IbadahHarian) {
        val details = item.detailSholatWajibs ?: emptyList()
        val jamaah = details.count { it.idStatusSholatWajib == 1 }.toFloat()
        val munfarid = details.count { it.idStatusSholatWajib == 2 }.toFloat()
        val tidak = details.count { it.idStatusSholatWajib == 3 }.toFloat()
        val belum = (5 - details.size).coerceAtLeast(0).toFloat()

        val entries = mutableListOf<PieEntry>()
        val colorsList = mutableListOf<Int>()

        if (jamaah > 0) { entries.add(PieEntry(jamaah, "Berjamaah")); colorsList.add("#059669".toColorInt()) }
        if (munfarid > 0) { entries.add(PieEntry(munfarid, "Munfarid")); colorsList.add("#EAB308".toColorInt()) }
        if (tidak > 0) { entries.add(PieEntry(tidak, "Tidak")); colorsList.add("#DC2626".toColorInt()) }
        if (belum > 0) { entries.add(PieEntry(belum, "Kosong")); colorsList.add("#94A3B8".toColorInt()) }

        val dataSet = PieDataSet(entries, "").apply {
            colors = colorsList
            valueTextColor = Color.WHITE
            valueTextSize = 11f
        }

        binding.pieChartWajibIndividu.apply {
            data = PieData(dataSet).apply { setValueFormatter(PercentFormatter(binding.pieChartWajibIndividu)) }
            setUsePercentValues(true)
            description.isEnabled = false
            centerText = "Wajib"
            animateY(800)
            invalidate()
        }

        val quranInfo = if (item.membacaAlquran) "Al-Qur'an: Sudah ✅" else "Al-Qur'an: BELUM DIISI ❌"
        updateRincianList("IBADAH WAJIB", listOf(quranInfo) + details.map { 
            val statusStr = if (it.status.isNullOrEmpty()) "BELUM DIISI ❌" else it.status
            String.format(Locale.US, "%s: %s", it.kategori, statusStr) 
        })
    }

    private fun processAndDisplaySunnah(list: List<IbadahSunnah>?) {
        // ID Kategori Sunnah: 1: Tarawih, 2: Witir, 3: Dhuha, 4: Tahajud, 5: Sedekah 🕌
        val statusMap = mutableMapOf(1 to false, 2 to false, 3 to false, 4 to false, 5 to false)
        val names = mapOf(1 to "Tarawih", 2 to "Witir", 3 to "Dhuha", 4 to "Tahajud", 5 to "Sedekah")

        list?.forEach { item ->
            // Gunakan field 'sudahDilakukan' dan 'idKategoriSunnah' sesuai JSON Monitoring terbaru 🚀
            if (item.sudahDilakukan) {
                statusMap[item.idKategoriSunnah] = true
            }
            // Support juga struktur detail nested jika dikirim backend
            item.detailIbadahSunnahs?.forEach { detail ->
                if (detail.sudahDilakukan) statusMap[detail.idKategoriSunnah] = true
            }
        }

        val doneCount = statusMap.values.count { it }.toFloat()
        val notDoneCount = (5 - doneCount).coerceAtLeast(0f)

        val entries = mutableListOf<PieEntry>()
        val colorsList = mutableListOf<Int>()
        
        if (doneCount > 0) { entries.add(PieEntry(doneCount, "Tuntas")); colorsList.add("#15803D".toColorInt()) }
        if (notDoneCount > 0) { entries.add(PieEntry(notDoneCount, "Belum")); colorsList.add("#DC2626".toColorInt()) }

        val dataSet = PieDataSet(entries, "").apply {
            colors = colorsList
            valueTextColor = Color.WHITE
        }

        binding.pieChartSunnahIndividu.apply {
            data = PieData(dataSet).apply { setValueFormatter(PercentFormatter(binding.pieChartSunnahIndividu)) }
            setUsePercentValues(true)
            description.isEnabled = false
            centerText = "Sunnah"
            animateY(1000)
            invalidate()
        }

        val rincianTexts = names.map { (id, name) -> 
            val ket = if (statusMap[id] == true) "MELAKSANAKAN ✅" else "TIDAK MELAKSANAKAN ❌"
            String.format(Locale.US, "%s: %s", name, ket)
        }
        updateRincianList("IBADAH SUNNAH", rincianTexts)
    }

    private fun updateRincianList(header: String, items: List<String>) {
        val headerView = TextView(this).apply {
            text = header; setPadding(0, 48, 0, 16); setTypeface(null, Typeface.BOLD); setTextColor("#004D40".toColorInt()); setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }
        binding.containerRincianSholat.addView(headerView)
        items.forEach { textItem ->
            val itemView = TextView(this).apply {
                text = String.format(Locale.US, "• %s", textItem); setPadding(16, 4, 0, 4)
                
                // WARNA MERAH & TEBAL JIKA BELUM DILAKSANAKAN 🔴
                if (textItem.contains("TIDAK", ignoreCase = true) || 
                    textItem.contains("BELUM", ignoreCase = true) || 
                    textItem.contains("❌")) {
                    setTextColor("#DC2626".toColorInt()) // Merah tajam
                    setTypeface(null, Typeface.BOLD)    // Tebalkan teks
                } else {
                    setTextColor("#1E293B".toColorInt()) // Warna default
                }
                
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
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
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            
            val titlePaint = Paint().apply { isAntiAlias = true; typeface = Typeface.DEFAULT_BOLD; textSize = 18f; color = Color.BLACK }
            val subPaint = Paint().apply { isAntiAlias = true; typeface = Typeface.DEFAULT_BOLD; textSize = 13f; color = Color.BLACK }
            val textPaint = Paint().apply { isAntiAlias = true; textSize = 11f; color = Color.BLACK }
            
            var y = 60f
            canvas.drawText("LAPORAN IBADAH PRIBADI", 50f, y, titlePaint)
            y += 30f
            canvas.drawText(String.format(Locale.US, "Nama Siswa : %s", ibadah.namaUser), 50f, y, textPaint)
            y += 20f
            canvas.drawText(String.format(Locale.US, "Laporan Per Tanggal : %s", formatNiceDate(activeDate)), 50f, y, textPaint)
            
            y += 40f
            canvas.drawText("Ibadah Wajib (Sholat 5 Waktu)", 50f, y, subPaint)
            y += 20f
            val quran = if (ibadah.membacaAlquran) "Sudah" else "Belum"
            canvas.drawText(String.format(Locale.US, "- Membaca Al-Qur'an: %s", quran), 65f, y, textPaint)
            y += 20f
            ibadah.detailSholatWajibs?.forEach {
                canvas.drawText(String.format(Locale.US, "- %s: %s", it.kategori, it.status ?: "Diisi"), 65f, y, textPaint)
                y += 20f
            }
            
            y += 30f
            canvas.drawText("Ibadah Sunnah Ramadhan", 50f, y, subPaint)
            y += 20f
            val sunnahNames = mapOf(1 to "Tarawih", 2 to "Witir", 3 to "Dhuha", 4 to "Tahajud", 5 to "Sedekah")
            val statusMap = mutableMapOf(1 to false, 2 to false, 3 to false, 4 to false, 5 to false)
            sunnahDataList?.forEach { s ->
                if (s.sudahDilakukan) statusMap[s.idKategoriSunnah] = true
                s.detailIbadahSunnahs?.forEach { if (it.sudahDilakukan) statusMap[it.idKategoriSunnah] = true }
            }
            sunnahNames.forEach { (id, name) ->
                val status = if (statusMap[id] == true) "Selesai" else "Belum"
                canvas.drawText(String.format(Locale.US, "- %s: %s", name, status), 65f, y, textPaint)
                y += 20f
            }
            
            pdfDocument.finishPage(page)
            savePdfFile(pdfDocument, String.format(Locale.US, "LAPORAN_%s_%s", ibadah.namaUser?.replace(" ","_"), activeDate))
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
                    fileUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    fileUri?.let { contentResolver.openOutputStream(it)?.use { out -> pdfDocument.writeTo(out) } }
                } else {
                    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                    FileOutputStream(file).use { out -> pdfDocument.writeTo(out) }
                    fileUri = FileProvider.getUriForFile(this@DetailStatistikSiswaActivity, String.format(Locale.US, "%s.provider", packageName), file)
                }
                withContext(Dispatchers.Main) { 
                    Toast.makeText(this@DetailStatistikSiswaActivity, "PDF Tersimpan di Downloads", Toast.LENGTH_LONG).show()
                    showNotification(fileName, fileUri)
                }
            } catch (e: Exception) { Log.e("SavePdf", "Error: ${e.message}") } finally { pdfDocument.close() }
        }
    }

    private fun showNotification(fileName: String, fileUri: Uri?) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val chanId = "individual_report"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(chanId, "Laporan", NotificationManager.IMPORTANCE_HIGH)
            nm.createNotificationChannel(channel)
        }
        val intent = Intent(Intent.ACTION_VIEW).apply { 
            setDataAndType(fileUri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val notif = NotificationCompat.Builder(this, chanId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Download Selesai")
            .setContentText(fileName)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .build()
        nm.notify(2002, notif)
    }
}
