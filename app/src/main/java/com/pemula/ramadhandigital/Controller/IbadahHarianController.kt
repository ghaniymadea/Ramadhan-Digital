package com.pemula.ramadhandigital.controller

import com.pemula.ramadhandigital.model.Account
import com.pemula.ramadhandigital.model.IbadahHarian
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class IbadahHarianController {
    private val services = Client.ibadahHarian

    suspend fun getIbadahHarianHariIni(): IbadahHarian? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            // Gunakan Locale.US agar format angka tanggal tidak berubah di region tertentu 🍌
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val currentDate = sdf.format(Date())

            val response = services.getIbadahHarian(token, currentDate)

            if (response.isSuccessful) {
                response.body()?.data
            } else if (response.code() == 404) {
                // Sesuai C#: return Results.NotFound(...) jika data belum diisi
                null
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun registerIbadahHarian(ibadah: IbadahHarian): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.registerIbadahHarian(token, ibadah)

            // Cek sukses (200 OK)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}