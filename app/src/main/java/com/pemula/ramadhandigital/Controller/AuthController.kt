package com.pemula.ramadhandigital.Controller

import com.pemula.ramadhandigital.services.Client
import com.pemula.ramadhandigital.model.Login
import com.pemula.ramadhandigital.model.LoginRespons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthController {
    private val services= Client.auth
    suspend fun loginController(login: Login): LoginRespons?= withContext(Dispatchers.IO){
        try {
            val  respons= services.Login_services(login)
            if (respons.isSuccessful){
                return@withContext respons.body()
            }else{
                return@withContext null
            }
        }catch (e: Exception){
            return@withContext null
        }
    }
}