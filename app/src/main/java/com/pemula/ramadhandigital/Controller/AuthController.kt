package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.model.*
import com.pemula.ramadhandigital.services.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthController {
    private val services = Client.auth

    /**
     * Login Controller
     * Menyesuaikan dengan backend C# yang mengembalikan LoginResponse 🍌
     */
    suspend fun loginController(login: Login): LoginRespons? = withContext(Dispatchers.IO) {
        try {
            val response = services.login(login)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("AuthController", "Login gagal: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthController", "Error login: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Register Admin (Hanya bisa jika sistem baru diinstall) 🍌🐒
     */
    suspend fun registerAdmin(request: RegisterRequest): CommonResponse? = withContext(Dispatchers.IO) {
        try {
            val response = services.registerAdmin(request)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Register Guru (Wajib Token Admin) 🔐
     */
    suspend fun registerGuru(request: RegisterRequest): CommonResponse? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.registerGuru(token, request)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Register Siswa (Wajib Token Admin) 🔐
     */
    suspend fun registerSiswa(request: RegisterRequest): CommonResponse? = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${Account.Token}"
            val response = services.registerSiswa(token, request)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}
