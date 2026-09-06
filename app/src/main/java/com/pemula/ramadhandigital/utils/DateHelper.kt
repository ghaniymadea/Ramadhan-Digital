package com.pemula.ramadhandigital.utils

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    /**
     * Format: yyyy-MM-dd
     * Digunakan untuk komunikasi dengan Backend ASP.NET Core (DateOnly)
     */
    private val apiDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    /**
     * Format: dd MMMM yyyy (Bahasa Indonesia)
     * Digunakan untuk tampilan di UI
     */
    private val displayDateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    /**
     * Mendapatkan tanggal hari ini dalam format yyyy-MM-dd
     */
    fun getTodayApi(): String = apiDateFormatter.format(Date())

    /**
     * Mendapatkan tanggal hari ini dalam format tampilan UI
     */
    fun getTodayDisplay(): String = displayDateFormatter.format(Date())

    /**
     * Konversi dari Date ke format API (yyyy-MM-dd)
     */
    fun toApiDate(date: Date): String = apiDateFormatter.format(date)

    /**
     * Konversi dari format API (yyyy-MM-dd atau ISO) ke format tampilan UI
     */
    fun toDisplayDate(dateStr: String?): String {
        if (dateStr.isNullOrEmpty()) return "-"
        return try {
            // Bersihkan format ISO (T00:00:00) jika ada
            val cleanDate = if (dateStr.contains("T")) dateStr.split("T")[0] else dateStr
            val date = apiDateFormatter.parse(cleanDate)
            displayDateFormatter.format(date!!)
        } catch (e: Exception) {
            dateStr
        }
    }

    /**
     * Konversi dari format tampilan UI ke format API (yyyy-MM-dd)
     */
    fun fromDisplayToApi(displayDate: String): String {
        return try {
            val date = displayDateFormatter.parse(displayDate)
            apiDateFormatter.format(date!!)
        } catch (e: Exception) {
            getTodayApi()
        }
    }

    /**
     * Mengambil bagian tanggal saja (10 karakter pertama) dari string ISO
     */
    fun stripTime(dateStr: String?): String? {
        if (dateStr == null) return null
        return if (dateStr.length >= 10) dateStr.substring(0, 10) else dateStr
    }
}
