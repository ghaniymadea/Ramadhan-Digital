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
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.gson.Gson
import com.pemula.ramadhandigital.controller.IbadahHarianController
import com.pemula.ramadhandigital.controller.IbadahSunnahController
import com.pemula.ramadhandigital.databinding.ActivityDetailSiswaBinding
import com.pemula.ramadhandigital.model.*
import com.pemula.ramadhandigital.utils.DateHelper
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

class DetailStatistikSiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSiswaBinding
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
        binding = ActivityDetailSiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SessionManager(this).syncToAccount()

        val jsonItem = intent.getStringExtra("ITEM_DATA")
        Log.d("DetailStatistik", "Data: $jsonItem")
        itemData = try { Gson().fromJson(jsonItem, IbadahHarian::class.java) } catch (e: Exception) { null }

        setupToolbar()
        setupSwipeRefresh()
        loadAllData(showProgress = true)

        binding.btnExportIndividual.setOnClickListener { checkPermissionAndExport() }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Statistik Ibadah Harian & Sunnah"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeColors("#004D40".toColorInt())
        binding.swipeRefresh.setOnRefreshListener { loadAllData(showProgress = false) }
    }

    private fun loadAllData(showProgress: Boolean = true) {
        val item = itemData ?: return
        val studentId = if (item.idUser != 0) item.idUser else item.id
        
        if (studentId == 0) {
            Toast.makeText(this, "ID Siswa tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (showProgress) binding.loadingProgress.visibility = View.VISIBLE
        binding.tvNamaSiswaHeader.text = item.namaUser ?: "Tanpa Nama"
        binding.tvDetailInitial.text = if (!item.namaUser.isNullOrEmpty()) item.namaUser.take(1).uppercase() else "?"

        lifecycleScope.launch {
            try {
                activeDate = DateHelper.stripTime(item.tanggal) ?: DateHelper.getTodayApi()
                binding.tvTanggalDetail.text = "Laporan Tanggal: ${DateHelper.toDisplayDate(activeDate)}"

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

    private fun displayWajibData(item: IbadahHarian) {
        val details = item.detailSholatWajibs ?: emptyList()
        val entries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()

        // Map status ID to count
        val jamaah = details.count { it.idStatusSholatWajib == 1 }.toFloat()
        val munfarid = details.count { it.idStatusSholatWajib == 2 }.toFloat()
        val tidak = details.count { it.idStatusSholatWajib == 3 }.toFloat()
        
        // Use reads from detail or from model fields if any
        val totalFilled = (jamaah + munfarid + tidak).toInt()
        val belum = (5 - totalFilled).coerceAtLeast(0).toFloat()

        if (jamaah > 0) { entries.add(PieEntry(jamaah, "Berjamaah")); colors.add("#059669".toColorInt()) }
        if (munfarid > 0) { entries.add(PieEntry(munfarid, "Munfarid")); colors.add("#EAB308".toColorInt()) }
        if (tidak > 0) { entries.add(PieEntry(tidak, "Tidak")); colors.add("#DC2626".toColorInt()) }
        if (belum > 0) { entries.add(PieEntry(belum, "Kosong")); colors.add("#94A3B8".toColorInt()) }

        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        binding.pieChartWajibIndividu.apply {
            data = PieData(dataSet).apply { setValueFormatter(PercentFormatter(binding.pieChartWajibIndividu)) }
            setUsePercentValues(true)
            description.isEnabled = false
            centerText = "Wajib"
            animateY(800)
            invalidate()
        }

        val quranInfo = if (item.membacaAlquran) {
            val surah = if (!item.targetBacaan.isNullOrBlank()) item.targetBacaan else "Sudah Membaca"
            "Membaca Al-Qur'an: $surah ✅"
        } else {
            "Membaca Al-Qur'an: Belum ❌"
        }
        val sholatList = details.map { "${it.kategori ?: "Sholat"}: ${it.status ?: "Belum Isi"}" }
        updateRincianList("IBADAH WAJIB", listOf(quranInfo) + sholatList)
    }

    private fun processAndDisplaySunnah(list: List<IbadahSunnah>?) {
        val statusMap = mutableMapOf(1 to false, 2 to false, 3 to false, 4 to false, 5 to false)
        val names = mapOf(1 to "Tarawih", 2 to "Witir", 3 to "Dhuha", 4 to "Tahajud", 5 to "Sedekah")

        list?.forEach { item ->
            // Support both direct item and nested details
            if (item.sudahDilakukan) statusMap[item.idKategoriSunnah] = true
            item.detailIbadahSunnahs?.forEach { if (it.sudahDilakukan) statusMap[it.idKategoriSunnah] = true }
        }

        val done = statusMap.values.count { it }.toFloat()
        val entries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()

        if (done > 0) { entries.add(PieEntry(done, "Tuntas")); colors.add("#15803D".toColorInt()) }
        val undone = (5 - done).coerceAtLeast(0f)
        if (undone > 0) { entries.add(PieEntry(undone, "Belum")); colors.add("#DC2626".toColorInt()) }

        val dataSet = PieDataSet(entries, "").apply { 
            this.colors = colors
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        binding.pieChartSunnahIndividu.apply {
            data = PieData(dataSet).apply { setValueFormatter(PercentFormatter(binding.pieChartSunnahIndividu)) }
            setUsePercentValues(true)
            description.isEnabled = false
            centerText = "Sunnah"
            animateY(1000)
            invalidate()
        }

        updateRincianList("IBADAH SUNNAH", names.map { (id, name) -> "$name: ${if (statusMap[id] == true) "MELAKSANAKAN ✅" else "TIDAK ❌"}" })
    }

    private fun updateRincianList(header: String, items: List<String>) {
        val head = TextView(this).apply { text = header; setPadding(0, 48, 0, 16); setTypeface(null, Typeface.BOLD); setTextColor("#0A3622".toColorInt()); setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f) }
        binding.containerRincianSholat.addView(head)

        items.forEach { text ->
            try {
                val v = layoutInflater.inflate(R.layout.item_amalan_detail, binding.containerRincianSholat, false)
                val parts = text.split(":")
                val amalanName = parts[0].trim().replace("• ", "")
                val statusText = if (parts.size > 1) parts[1].trim() else ""
                
                v.findViewById<TextView>(R.id.tvAmalanName).text = amalanName
                v.findViewById<TextView>(R.id.tvAmalanStatus).text = "Status: $statusText"
                
                val ivStatus = v.findViewById<ImageView>(R.id.ivAmalanStatusIcon)
                val isDone = statusText.contains("✅") || statusText.contains("Sudah") || statusText.contains("MELAKSANAKAN") || statusText.contains("Berjamaah") || statusText.contains("Munfarid")
                
                ivStatus.visibility = View.VISIBLE
                ivStatus.setImageResource(if (isDone) R.drawable.ic_checked_circle else R.drawable.ic_unchecked_circle)
                ivStatus.setColorFilter(if (isDone) "#059669".toColorInt() else "#94A3B8".toColorInt())

                binding.containerRincianSholat.addView(v)
            } catch (e: Exception) {
                Log.e("DetailStatistik", "Error inflation: ${e.message}")
            }
        }
    }

    private fun checkPermissionAndExport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else startExport()
    }

    private fun startExport() {
        val ibadah = currentIbadahFull ?: return
        lifecycleScope.launch {
            val pdf = PdfDocument()
            val page = pdf.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
            val canvas = page.canvas
            val p = Paint().apply { isAntiAlias = true; textSize = 12f }
            canvas.drawText("LAPORAN IBADAH: ${ibadah.namaUser}", 50f, 50f, p)
            pdf.finishPage(page)
            savePdfFile(pdf, "LAPORAN_${ibadah.namaUser?.replace(" ","_")}")
        }
    }

    private fun savePdfFile(pdf: PdfDocument, prefix: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val fileName = "${prefix}_${System.currentTimeMillis()}.pdf"
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    uri?.let { contentResolver.openOutputStream(it)?.use { out -> pdf.writeTo(out) } }
                } else {
                    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                    FileOutputStream(file).use { out -> pdf.writeTo(out) }
                }
                withContext(Dispatchers.Main) { Toast.makeText(this@DetailStatistikSiswaActivity, "PDF Tersimpan", Toast.LENGTH_SHORT).show() }
            } catch (e: Exception) { Log.e("PDF", "Error: ${e.message}") } finally { pdf.close() }
        }
    }
}
