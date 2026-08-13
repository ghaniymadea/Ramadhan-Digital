package com.pemula.ramadhandigital.controller

import android.util.Log
import com.pemula.ramadhandigital.services.Client
import com.pemula.ramadhandigital.model.Login
import com.pemula.ramadhandigital.model.LoginRespons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthController {
    private val services = Client.auth
    
    suspend fun loginController(login: Login): LoginRespons? = withContext(Dispatchers.IO) {
        try {
            val response = services.Login_services(login)
            if (response.isSuccessful) {
                return@withContext response.body()
            } else {
                Log.e("AuthController", "Login gagal: ${response.code()}")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e("AuthController", "Error koneksi", e)
            return@withContext null
        }
    }
}