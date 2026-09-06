package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.services.Client
import com.pemula.ramadhandigital.utils.DateHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class IbadahHarianController {
    private val services = Client.ibadahHarian

    suspend fun getIbadahHarianByDate(tanggal: String?): IbadahHarian? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val date = DateHelper.stripTime(tanggal)
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
            val date = DateHelper.stripTime(tanggal)
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
            val date = DateHelper.stripTime(tanggal)
            val response = services.getMonitoringKelas(token, idKelas, date)
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) { null }
    }

    suspend fun registerIbadahHarian(ibadah: IbadahHarian): Boolean = withContext(Dispatchers.IO) {
        try {
            val tokenRaw = Account.Token
            if (tokenRaw.isNullOrEmpty()) return@withContext false
            
            val token = "Bearer $tokenRaw"
            ibadah.idUser = Account.Id
            
            // Backend expects yyyy-MM-dd for DateOnly 🚀
            val requestData = ibadah.copy(tanggal = DateHelper.stripTime(ibadah.tanggal))
            
            Log.d("IbadahHarian", "Saving: IdUser=${requestData.idUser}, Tanggal=${requestData.tanggal}")
            val response = services.registerIbadahHarian(token, requestData)
            if (!response.isSuccessful) {
                Log.e("IbadahHarian", "Failed: ${response.code()} - ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("IbadahHarian", "Error: ${e.localizedMessage}")
            false
        }
    }
}
