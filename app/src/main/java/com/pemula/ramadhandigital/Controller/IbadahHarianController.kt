package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class IbadahHarianController {
    private val services = Client.ibadahHarian

    /**
     * Membersihkan string tanggal dari format ISO (T00:00:00) 
     * Tetap menggunakan format yyyy-MM-dd untuk monitoring Harian 🚀
     */
    private fun cleanDate(dateStr: String?): String? {
        if (dateStr == null) return null
        return if (dateStr.contains("T")) dateStr.split("T")[0] else dateStr
    }

    suspend fun getIbadahHarianByDate(tanggal: String?): IbadahHarian? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val date = cleanDate(tanggal)
            val response = services.getIbadahHarian(token, date)
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) { null }
    }

    /**
     * Guru: Ambil rekap 1 siswa (yyyy-MM-dd)
     */
    suspend fun getRekapSiswaSingleDate(idSiswa: Int, tanggal: String): IbadahHarian? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val date = cleanDate(tanggal)
            val response = services.getRekapSiswa(token, idSiswa, date, date)
            if (response.isSuccessful) response.body()?.data?.firstOrNull() else null
        } catch (e: Exception) { null }
    }

    /**
     * Guru: Ambil monitoring 1 kelas (Daftar Siswa)
     * Menggunakan format yyyy-MM-dd agar data muncul kembali 🕵️‍♂️
     */
    suspend fun getMonitoringKelas(idKelas: Int, tanggal: String): List<IbadahHarian>? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val date = cleanDate(tanggal)
            val response = services.getMonitoringKelas(token, idKelas, date)
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) { null }
    }

    suspend fun registerIbadahHarian(ibadah: IbadahHarian): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            ibadah.idUser = Account.Id
            val response = services.registerIbadahHarian(token, ibadah)
            response.isSuccessful
        } catch (e: Exception) { false }
    }
}
